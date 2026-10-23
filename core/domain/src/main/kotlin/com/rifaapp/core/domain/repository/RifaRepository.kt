package com.rifaapp.core.domain.repository

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import kotlinx.coroutines.flow.Flow

interface RifaRepository {

    /** La rifa activa (v1 maneja una sola). Emite null mientras no exista. */
    fun observarActiva(): Flow<Rifa?>

    suspend fun obtenerActiva(): Rifa?

    suspend fun guardar(rifa: Rifa): Resultado<Rifa>
}
