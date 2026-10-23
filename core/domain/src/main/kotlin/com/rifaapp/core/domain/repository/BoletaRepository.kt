package com.rifaapp.core.domain.repository

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * La boleta es el agregado: sus pagos se registran a través de este repositorio
 * para que nunca queden desincronizados del estado.
 */
interface BoletaRepository {

    fun observarPorRifa(rifaId: Long): Flow<List<Boleta>>

    suspend fun obtenerPorNumero(rifaId: Long, numero: Int): Boleta?

    suspend fun guardar(boleta: Boleta): Resultado<Boleta>

    suspend fun registrarPago(
        boletaId: Long,
        monto: Long,
        fecha: Instant,
        nota: String? = null,
    ): Resultado<Boleta>

    /** Elimina la boleta activa; el número vuelve a estar DISPONIBLE. */
    suspend fun liberar(boletaId: Long): Resultado<Unit>
}
