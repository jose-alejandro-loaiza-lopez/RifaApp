package com.rifaapp.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class EstadoNumeroTest {

    @Test
    fun `sin pagos el numero esta reservado`() {
        assertEquals(EstadoNumero.RESERVADO, EstadoNumero.desde(montoPagado = 0L, precioBoleta = 10_000L))
    }

    @Test
    fun `pago parcial deja el numero abonado`() {
        assertEquals(EstadoNumero.ABONADO, EstadoNumero.desde(montoPagado = 4_000L, precioBoleta = 10_000L))
    }

    @Test
    fun `pago exacto deja el numero pagado`() {
        assertEquals(EstadoNumero.PAGADO, EstadoNumero.desde(montoPagado = 10_000L, precioBoleta = 10_000L))
    }

    @Test
    fun `pagar de mas sigue estando pagado`() {
        assertEquals(EstadoNumero.PAGADO, EstadoNumero.desde(montoPagado = 12_000L, precioBoleta = 10_000L))
    }

    @Test
    fun `si la rifa es gratis cualquier pago la deja pagada`() {
        assertEquals(EstadoNumero.PAGADO, EstadoNumero.desde(montoPagado = 1L, precioBoleta = 0L))
    }

    @Test
    fun `al subir el precio un numero pagado vuelve a abonado`() {
        val pagado: Long = 10_000L
        assertEquals(EstadoNumero.PAGADO, EstadoNumero.desde(pagado, precioBoleta = 10_000L))
        assertEquals(EstadoNumero.ABONADO, EstadoNumero.desde(pagado, precioBoleta = 15_000L))
    }
}
