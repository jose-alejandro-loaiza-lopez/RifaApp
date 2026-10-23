package com.rifaapp.core.domain.model

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CoberturaModelosTest {

    @Test
    fun `ejercita propiedades y metodos de modelos`() {
        val rifa = Rifa(
            id = 1L,
            titulo = "Rifa",
            descripcion = "Desc",
            fechaSorteo = LocalDate.of(2026, 1, 1),
            precioBoleta = 10_000L,
            numeroInicio = 0,
            numeroFin = 9,
            cantidadDigitos = 2,
            columnas = 3,
            loteria = "Lotto",
        )

        assertEquals("Desc", rifa.descripcion)
        assertNotNull(rifa.fechaSorteo)
        assertEquals(3, rifa.columnas)
        assertEquals("Lotto", rifa.loteria)
        assertEquals("07", rifa.formatear(7))
        assertEquals(10, rifa.cantidadNumeros)
        assertTrue(rifa.contiene(5))
        org.junit.Assert.assertFalse(rifa.contiene(100))

        val pago = Pago(id = 1L, boletaId = 1L, monto = 1_000L, fecha = java.time.Instant.EPOCH, nota = "n")
        assertEquals(1L, pago.id)
        assertEquals(1L, pago.boletaId)
        assertEquals(java.time.Instant.EPOCH, pago.fecha)
        assertEquals(1_000L, pago.monto)
        assertEquals("n", pago.nota)

        val boleta = Boleta(
            id = 1L,
            rifaId = 1L,
            numero = 7,
            cliente = Cliente("Ana", "+573001234567"),
            pagos = listOf(pago),
            notas = "nota",
        )
        assertEquals(1_000L, boleta.montoPagado)
        assertEquals(EstadoNumero.ABONADO, boleta.estado(10_000L))
        assertEquals(9_000L, boleta.saldo(10_000L))
        assertEquals("nota", boleta.notas)

        val numero = Numero(valor = 7, estado = EstadoNumero.PAGADO)
        assertEquals(7, numero.valor)

        val tablero = rifa.construirTablero(listOf(boleta))
        assertNotNull(tablero.resumen)
        assertEquals(10, tablero.numeros.size)
        assertEquals(1, tablero.boletas.size)
    }

    private fun assertTrue(condition: Boolean) = org.junit.Assert.assertTrue(condition)
}