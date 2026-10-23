package com.rifaapp.core.data

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Pago
import com.rifaapp.core.domain.repository.BoletaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant

/**
 * Implementación en memoria. Mantiene el agregado Boleta con sus pagos.
 * Los estados no se guardan: salen de [Boleta.estado] al construir el tablero.
 */
class InMemoryBoletaRepository(
    boletasIniciales: List<Boleta> = emptyList(),
) : BoletaRepository {

    private val boletas = MutableStateFlow(boletasIniciales)
    private var siguienteBoletaId = (boletasIniciales.maxOfOrNull { it.id } ?: 0L) + 1L
    private var siguientePagoId = (boletasIniciales.flatMap { it.pagos }.maxOfOrNull { it.id } ?: 0L) + 1L

    override fun observarPorRifa(rifaId: Long): Flow<List<Boleta>> =
        boletas.map { lista -> lista.filter { it.rifaId == rifaId } }

    override suspend fun obtenerPorNumero(rifaId: Long, numero: Int): Boleta? =
        boletas.value.firstOrNull { it.rifaId == rifaId && it.numero == numero }

    override suspend fun obtenerPorRifa(rifaId: Long): List<Boleta> =
        boletas.value.filter { it.rifaId == rifaId }

    override suspend fun guardar(boleta: Boleta): Resultado<Boleta> {
        val nueva = boleta.copy(id = siguienteBoletaId++)
        boletas.value = boletas.value + nueva
        return Resultado.Exito(nueva)
    }

    override suspend fun registrarPago(
        boletaId: Long,
        monto: Long,
        fecha: Instant,
        nota: String?,
    ): Resultado<Boleta> {
        val actual = boletas.value.firstOrNull { it.id == boletaId }
            ?: return Resultado.Fallo(ErrorDominio.BoletaNoEncontrada)

        val pago = Pago(
            id = siguientePagoId++,
            boletaId = boletaId,
            monto = monto,
            fecha = fecha,
            nota = nota,
        )
        val actualizada = actual.copy(pagos = actual.pagos + pago)
        boletas.value = boletas.value.map { if (it.id == boletaId) actualizada else it }
        return Resultado.Exito(actualizada)
    }

    override suspend fun liberar(boletaId: Long): Resultado<Unit> {
        boletas.value = boletas.value.filterNot { it.id == boletaId }
        return Resultado.Exito(Unit)
    }
}
