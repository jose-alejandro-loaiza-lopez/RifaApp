package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.repository.RifaRepository
import com.rifaapp.core.domain.validation.ValidadorRifa

/**
 * Guarda la configuración de la rifa (título, fecha, precio, rango).
 * Al cambiar el precio, todos los estados se recalculan porque son derivados.
 */
class GuardarConfiguracionRifa(
    private val rifas: RifaRepository,
) {
    suspend operator fun invoke(rifa: Rifa): Resultado<Rifa> = when (val validacion = ValidadorRifa.validar(rifa)) {
        is Resultado.Fallo -> validacion
        is Resultado.Exito -> rifas.guardar(validacion.valor)
    }
}
