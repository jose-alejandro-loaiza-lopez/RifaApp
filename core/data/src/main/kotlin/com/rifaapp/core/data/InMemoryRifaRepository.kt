package com.rifaapp.core.data

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.repository.RifaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Implementación en memoria, pensada para la rebanada vertical.
 * Se reemplaza por Room en la Fase 2 sin tocar el dominio ni la UI.
 */
class InMemoryRifaRepository(
    rifaInicial: Rifa,
) : RifaRepository {

    private val estado = MutableStateFlow<Rifa?>(rifaInicial)

    override fun observarActiva(): Flow<Rifa?> = estado

    override suspend fun obtenerActiva(): Rifa? = estado.value

    override suspend fun guardar(rifa: Rifa): Resultado<Rifa> {
        estado.value = rifa
        return Resultado.Exito(rifa)
    }
}
