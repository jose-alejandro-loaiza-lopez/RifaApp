package com.rifaapp.core.domain.validation

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa

object ValidadorRifa {

    private const val MAX_NUMEROS = 10_000

    fun validar(rifa: Rifa): Resultado<Rifa> = when {
        rifa.titulo.isBlank() -> Resultado.Fallo(ErrorDominio.TituloVacio)
        rifa.precioBoleta < 0L -> Resultado.Fallo(ErrorDominio.PrecioInvalido)
        rifa.numeroInicio < 0 -> Resultado.Fallo(ErrorDominio.RangoInvalido)
        rifa.numeroFin < rifa.numeroInicio -> Resultado.Fallo(ErrorDominio.RangoInvalido)
        rifa.cantidadNumeros > MAX_NUMEROS -> Resultado.Fallo(ErrorDominio.RangoInvalido)
        rifa.cantidadDigitos !in 1..6 -> Resultado.Fallo(ErrorDominio.RangoInvalido)
        else -> Resultado.Exito(rifa)
    }
}
