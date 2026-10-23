package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.EstadoNumero
import com.rifaapp.core.domain.model.Pago
import com.rifaapp.core.domain.model.rifaBase
import com.rifaapp.core.domain.repository.BoletaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservarNumeroTest {

    private val rifa = rifaBase()

    @Test
    fun `reserva un numero libre`() = runTest {
        val repo = FakeBoletaRepository()
        val caso = ReservarNumero(repo)

        val resultado = caso(rifa, numero = 7, nombre = "Ana", telefono = "300 123 4567")

        assertTrue(resultado is Resultado.Exito)
        val boleta = (resultado as Resultado.Exito).valor
        assertEquals(7, boleta.numero)
        assertEquals("Ana", boleta.cliente.nombre)
        assertEquals("+3001234567", boleta.cliente.telefonoE164)
        assertEquals(EstadoNumero.RESERVADO, boleta.estado(rifa.precioBoleta))
    }

    @Test
    fun `rechaza numero fuera de rango`() = runTest {
        val resultado = ReservarNumero(FakeBoletaRepository())(rifa, 100, "Ana", "+573001234567")
        assertEquals(ErrorDominio.NumeroFueraDeRango, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza nombre vacio`() = runTest {
        val resultado = ReservarNumero(FakeBoletaRepository())(rifa, 5, "  ", "+573001234567")
        assertEquals(ErrorDominio.NombreVacio, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza telefono invalido`() = runTest {
        val resultado = ReservarNumero(FakeBoletaRepository())(rifa, 5, "Ana", "sin numero")
        assertEquals(ErrorDominio.TelefonoInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `no permite reservar dos veces el mismo numero`() = runTest {
        val repo = FakeBoletaRepository()
        val caso = ReservarNumero(repo)

        caso(rifa, 5, "Ana", "+573001234567")
        val segundo = caso(rifa, 5, "Luis", "+573009999999")

        assertEquals(ErrorDominio.NumeroNoDisponible, (segundo as Resultado.Fallo).error)
    }
}

internal class FakeBoletaRepository : BoletaRepository {

    private val boletas = MutableStateFlow<List<Boleta>>(emptyList())
    private var siguienteId = 1L

    override fun observarPorRifa(rifaId: Long): Flow<List<Boleta>> =
        boletas.map { lista -> lista.filter { it.rifaId == rifaId } }

    override suspend fun obtenerPorNumero(rifaId: Long, numero: Int): Boleta? =
        boletas.value.firstOrNull { it.rifaId == rifaId && it.numero == numero }

    override suspend fun obtenerPorRifa(rifaId: Long): List<Boleta> =
        boletas.value.filter { it.rifaId == rifaId }

    override suspend fun guardar(boleta: Boleta): Resultado<Boleta> {
        val nueva = boleta.copy(id = siguienteId++)
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

        val actualizada = actual.copy(
            pagos = actual.pagos + Pago(boletaId = boletaId, monto = monto, fecha = fecha, nota = nota),
        )
        boletas.value = boletas.value.map { if (it.id == boletaId) actualizada else it }
        return Resultado.Exito(actualizada)
    }

    override suspend fun liberar(boletaId: Long): Resultado<Unit> {
        boletas.value = boletas.value.filterNot { it.id == boletaId }
        return Resultado.Exito(Unit)
    }
}
