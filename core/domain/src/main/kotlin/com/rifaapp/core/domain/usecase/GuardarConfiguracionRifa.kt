package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.repository.BoletaRepository
import com.rifaapp.core.domain.repository.RifaRepository
import com.rifaapp.core.domain.validation.ValidadorRifa

/**
 * Guarda la configuración de la rifa (título, fecha, precio, rango).
 *
 * Dos reglas importantes:
 * - Al cambiar el precio no hay que recalcular nada: los estados son derivados.
 * - No se puede encoger el rango si quedan boletas por fuera; primero se liberan.
 */
class GuardarConfiguracionRifa(
    private val rifas: RifaRepository,
    private val boletas: BoletaRepository,
) {
    suspend operator fun invoke(rifa: Rifa): Resultado<Rifa> {
        val validacion = ValidadorRifa.validar(rifa)
        if (validacion is Resultado.Fallo) return validacion

        val fueraDeRango = boletas.obtenerPorRifa(rifa.id)
            .map { it.numero }
            .filterNot { rifa.contiene(it) }
            .sorted()

        if (fueraDeRango.isNotEmpty()) {
            return Resultado.Fallo(ErrorDominio.NumerosOcupadosFueraDeRango(fueraDeRango))
        }

        return rifas.guardar(rifa)
    }
}
