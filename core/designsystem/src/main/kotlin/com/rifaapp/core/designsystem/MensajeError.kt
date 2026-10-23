package com.rifaapp.core.designsystem

import com.rifaapp.core.domain.common.ErrorDominio

/** Traduce errores de negocio a mensajes para el usuario. */
fun ErrorDominio.textoVisible(): String = when (this) {
    ErrorDominio.NumeroNoDisponible -> "Ese número ya está ocupado."
    ErrorDominio.NumeroFueraDeRango -> "El número está fuera del rango de la rifa."
    ErrorDominio.NombreVacio -> "Escribe el nombre del cliente."
    ErrorDominio.TituloVacio -> "La rifa necesita un título."
    ErrorDominio.TelefonoInvalido -> "Teléfono inválido. Usa el indicativo, ej. +57 300 123 4567."
    ErrorDominio.MontoInvalido -> "El monto debe ser mayor que cero."
    ErrorDominio.PrecioInvalido -> "El precio de la boleta no es válido."
    ErrorDominio.RangoInvalido -> "El rango de números no es válido."
    ErrorDominio.RifaNoEncontrada -> "No hay una rifa configurada."
    ErrorDominio.BoletaNoEncontrada -> "No se encontró la boleta."
    is ErrorDominio.NumerosOcupadosFueraDeRango ->
        "No puedes dejar por fuera números con boleta: ${numeros.joinToString(", ")}. Libéralos primero."
    is ErrorDominio.Desconocido -> mensaje
}
