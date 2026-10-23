package com.rifaapp.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormatoTest {

    @Test
    fun `formatea centavos a pesos`() {
        assertEquals("$10.000", formatearPrecio(1_000_000L))
        assertEquals("$0", formatearPrecio(0L))
    }

    @Test
    fun `parsea pesos a centavos`() {
        assertEquals(1_000_000L, parsearPesosACentavos("10000"))
        assertEquals(1_000_000L, parsearPesosACentavos("10.000"))
        assertNull(parsearPesosACentavos(""))
        assertNull(parsearPesosACentavos("abc"))
    }
}