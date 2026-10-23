package com.rifaapp.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TableroTest {

    private val rifa = rifaBase()

    @Test
    fun `sin boletas todos los numeros estan disponibles`() {
        val tablero = rifa.construirTablero(emptyList())

        assertEquals(100, tablero.numeros.size)
        assertEquals(100, tablero.resumen.disponibles)
        assertEquals(0, tablero.resumen.vendidos)
        assertEquals(0L, tablero.recaudado)
    }

    @Test
    fun `cruza boletas con numeros y calcula el resumen`() {
        val boletas = listOf(
            boleta(numero = 0, pagado = 0L),        // reservado
            boleta(numero = 1, pagado = 5_000L),    // abonado
            boleta(numero = 2, pagado = 10_000L),   // pagado
        )

        val tablero = rifa.construirTablero(boletas)

        assertEquals(EstadoNumero.RESERVADO, tablero.numeros.first { it.valor == 0 }.estado)
        assertEquals(EstadoNumero.ABONADO, tablero.numeros.first { it.valor == 1 }.estado)
        assertEquals(EstadoNumero.PAGADO, tablero.numeros.first { it.valor == 2 }.estado)

        assertEquals(97, tablero.resumen.disponibles)
        assertEquals(1, tablero.resumen.reservados)
        assertEquals(1, tablero.resumen.abonados)
        assertEquals(1, tablero.resumen.pagados)
        assertEquals(3, tablero.resumen.vendidos)
        assertEquals(15_000L, tablero.resumen.recaudado)
        assertEquals(1_000_000L, tablero.resumen.potencial)
    }

    private fun boleta(numero: Int, pagado: Long): Boleta {
        val pagos = if (pagado > 0L) {
            listOf(Pago(boletaId = 1L, monto = pagado, fecha = Instant.EPOCH))
        } else {
            emptyList()
        }
        return Boleta(
            id = numero.toLong() + 1,
            rifaId = rifa.id,
            numero = numero,
            cliente = Cliente("Cliente $numero", "+573001234567"),
            pagos = pagos,
        )
    }
}

internal fun rifaBase(): Rifa = Rifa(
    id = 1L,
    titulo = "Rifa de prueba",
    precioBoleta = 10_000L,
    numeroInicio = 0,
    numeroFin = 99,
    cantidadDigitos = 2,
)
