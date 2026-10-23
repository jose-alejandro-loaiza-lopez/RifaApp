package com.rifaapp.core.domain.model

/** Persona que compra o reserva un número. */
data class Cliente(
    val nombre: String,
    /** Teléfono en formato E.164, ej. +573001234567 */
    val telefonoE164: String,
)
