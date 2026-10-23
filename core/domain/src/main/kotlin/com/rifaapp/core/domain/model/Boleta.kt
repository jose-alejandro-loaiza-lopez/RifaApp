package com.rifaapp.core.domain.model

/**
 * Asignación de un número a un cliente.
 * Los pagos viven dentro de la boleta: la suma de ellos define el estado.
 */
data class Boleta(
    val id: Long = 0L,
    val rifaId: Long,
    val numero: Int,
    val cliente: Cliente,
    val pagos: List<Pago> = emptyList(),
    val notas: String? = null,
) {
    val montoPagado: Long get() = pagos.sumOf { it.monto }

    fun estado(precioBoleta: Long): EstadoNumero = EstadoNumero.desde(montoPagado, precioBoleta)

    fun saldo(precioBoleta: Long): Long = (precioBoleta - montoPagado).coerceAtLeast(0L)
}
