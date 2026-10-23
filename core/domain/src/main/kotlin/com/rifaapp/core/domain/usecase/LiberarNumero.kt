package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.repository.BoletaRepository

/** El cliente ya no quiere participar: el número vuelve a estar disponible. */
class LiberarNumero(
    private val boletas: BoletaRepository,
) {
    suspend operator fun invoke(boletaId: Long): Resultado<Unit> = boletas.liberar(boletaId)
}
