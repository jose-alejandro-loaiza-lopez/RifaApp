package com.rifaapp.core.domain.model

/** Casilla del tablero. Existe siempre, tenga boleta o no. */
data class Numero(
    val valor: Int,
    val estado: EstadoNumero,
)
