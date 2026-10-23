package com.rifaapp.core.domain.validation

/**
 * Normaliza y valida teléfonos en formato E.164 (ej. +573001234567).
 * E.164: '+' seguido de 8 a 15 dígitos, empezando por un dígito distinto de 0.
 */
object ValidadorTelefono {

    private val FORMATO_E164 = Regex("^\\+[1-9]\\d{7,14}$")

    private val SEPARADORES = setOf(' ', '-', '(', ')', '.')

    fun esValido(telefono: String): Boolean = FORMATO_E164.matches(telefono)

    /**
     * Limpia separadores y agrega el '+' si falta.
     * Devuelve null si el resultado no es un E.164 válido.
     */
    fun normalizar(entrada: String): String? {
        val limpio = entrada.filterNot { it in SEPARADORES }
        if (limpio.isEmpty()) return null
        val conIndicativo = if (limpio.startsWith("+")) limpio else "+$limpio"
        return conIndicativo.takeIf(::esValido)
    }
}
