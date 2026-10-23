package com.rifaapp.core.domain.model

import java.time.LocalDate

/**
 * Configuración de la rifa. Todos los campos son editables en cualquier momento.
 */
data class Rifa(
    val id: Long = 0L,
    val titulo: String,
    val descripcion: String = "",
    val fechaSorteo: LocalDate? = null,
    /** Precio en centavos para evitar errores de punto flotante. */
    val precioBoleta: Long,
    val numeroInicio: Int = 0,
    val numeroFin: Int = 99,
    val cantidadDigitos: Int = 2,
) {
    val cantidadNumeros: Int get() = numeroFin - numeroInicio + 1

    fun contiene(numero: Int): Boolean = numero in numeroInicio..numeroFin

    /** 7 -> "07" cuando cantidadDigitos = 2. */
    fun formatear(numero: Int): String = numero.toString().padStart(cantidadDigitos, '0')
}
