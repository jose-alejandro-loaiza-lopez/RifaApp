package com.rifaapp.core.domain.model

/**
 * Construye el tablero cruzando la rifa (casillas) con las boletas (asignaciones).
 * El estado de cada casilla sale de su boleta; si no hay boleta, está DISPONIBLE.
 */
fun Rifa.construirTablero(boletas: List<Boleta>): Tablero {
    val porNumero = boletas.associateBy { it.numero }

    val numeros = (numeroInicio..numeroFin).map { valor ->
        val boleta = porNumero[valor]
        Numero(
            valor = valor,
            estado = boleta?.estado(precioBoleta) ?: EstadoNumero.DISPONIBLE,
        )
    }

    return Tablero(
        rifa = this,
        numeros = numeros,
        recaudado = boletas.sumOf { it.montoPagado },
        boletas = boletas,
    )
}
