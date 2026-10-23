package com.rifaapp.core.domain.common

/**
 * Resultado de una operación de dominio.
 * Evita usar excepciones para errores esperados (regla de negocio, validación).
 */
sealed interface Resultado<out T> {

    data class Exito<T>(val valor: T) : Resultado<T>

    data class Fallo(val error: ErrorDominio) : Resultado<Nothing>
}

/** Errores de negocio que la UI puede traducir a un mensaje. */
sealed interface ErrorDominio {

    data object NumeroNoDisponible : ErrorDominio

    data object NumeroFueraDeRango : ErrorDominio

    data object NombreVacio : ErrorDominio

    data object TituloVacio : ErrorDominio

    data object TelefonoInvalido : ErrorDominio

    data object MontoInvalido : ErrorDominio

    data object PrecioInvalido : ErrorDominio

    data object RangoInvalido : ErrorDominio

    data object RifaNoEncontrada : ErrorDominio

    data object BoletaNoEncontrada : ErrorDominio

    /** Se intentó encoger el rango de la rifa dejando boletas por fuera. */
    data class NumerosOcupadosFueraDeRango(val numeros: List<Int>) : ErrorDominio

    data class Desconocido(val mensaje: String) : ErrorDominio
}

inline fun <T> Resultado<T>.alExito(accion: (T) -> Unit): Resultado<T> {
    if (this is Resultado.Exito) accion(valor)
    return this
}

inline fun <T> Resultado<T>.alFallo(accion: (ErrorDominio) -> Unit): Resultado<T> {
    if (this is Resultado.Fallo) accion(error)
    return this
}
