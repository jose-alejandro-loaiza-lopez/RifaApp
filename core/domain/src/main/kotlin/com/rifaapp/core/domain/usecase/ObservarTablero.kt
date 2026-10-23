package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.model.Tablero
import com.rifaapp.core.domain.model.construirTablero
import com.rifaapp.core.domain.repository.BoletaRepository
import com.rifaapp.core.domain.repository.RifaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** Observa la rifa activa y sus boletas, emitiendo el tablero ya armado. */
class ObservarTablero(
    private val rifas: RifaRepository,
    private val boletas: BoletaRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Tablero?> = rifas.observarActiva().flatMapLatest { rifa ->
        if (rifa == null) {
            flowOf(null)
        } else {
            boletas.observarPorRifa(rifa.id).map { lista -> rifa.construirTablero(lista) }
        }
    }
}
