package com.rifaapp.core.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorTelefonoTest {

    @Test
    fun `acepta un E164 correcto`() {
        assertTrue(ValidadorTelefono.esValido("+573001234567"))
    }

    @Test
    fun `rechaza un telefono sin indicativo`() {
        assertEquals(false, ValidadorTelefono.esValido("3001234567"))
    }

    @Test
    fun `quita espacios guiones y parentesis`() {
        assertEquals("+573001234567", ValidadorTelefono.normalizar("+57 (300) 123-4567"))
    }

    @Test
    fun `agrega el mas si falta`() {
        assertEquals("+573001234567", ValidadorTelefono.normalizar("573001234567"))
    }

    @Test
    fun `rechaza letras`() {
        assertNull(ValidadorTelefono.normalizar("llamame"))
    }

    @Test
    fun `rechaza cadena vacia`() {
        assertNull(ValidadorTelefono.normalizar("   "))
    }
}
