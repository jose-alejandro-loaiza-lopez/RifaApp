package com.rifaapp.core.designsystem

import java.text.NumberFormat
import java.util.Locale

private val formatoMoneda: NumberFormat = NumberFormat.getIntegerInstance(Locale("es", "CO"))

/** 1000000 centavos -> "$10.000" */
fun formatearPrecio(centavos: Long): String = "$" + formatoMoneda.format(centavos / 100)

/** "10.000" (pesos) -> 1_000_000 centavos. Null si no hay dígitos. */
fun parsearPesosACentavos(texto: String): Long? {
    val digitos = texto.filter { it.isDigit() }
    if (digitos.isEmpty()) return null
    return digitos.toLongOrNull()?.times(100)
}

/** Precio en centavos -> texto editable en pesos (sin separadores). */
fun centavosAPesosTexto(centavos: Long): String = (centavos / 100).toString()
