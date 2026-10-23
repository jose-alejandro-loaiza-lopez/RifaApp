package com.rifaapp.core.domain.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultadoTest {

    @Test
    fun `alExito ejecuta la accion en exito`() {
        var ejecutado = false
        val resultado = Resultado.Exito(42).alExito { ejecutado = true }
        assertTrue(ejecutado)
        assertEquals(Resultado.Exito(42), resultado)
    }

    @Test
    fun `alExito no ejecuta la accion en fallo`() {
        var ejecutado = false
        val resultado = Resultado.Fallo(ErrorDominio.MontoInvalido).alExito { ejecutado = true }
        assertTrue(!ejecutado)
        assertTrue(resultado is Resultado.Fallo)
    }

    @Test
    fun `alFallo ejecuta la accion en fallo`() {
        var ejecutado = false
        val resultado = Resultado.Fallo(ErrorDominio.MontoInvalido).alFallo { ejecutado = true }
        assertTrue(ejecutado)
        assertTrue(resultado is Resultado.Fallo)
    }

    @Test
    fun `alFallo no ejecuta la accion en exito`() {
        var ejecutado = false
        val resultado = Resultado.Exito(42).alFallo { ejecutado = true }
        assertTrue(!ejecutado)
        assertEquals(Resultado.Exito(42), resultado)
    }

    @Test
    fun `cubre variantes de error`() {
        val desconocido = ErrorDominio.Desconocido("boom")
        assertEquals("boom", desconocido.mensaje)
        val fuera = ErrorDominio.NumerosOcupadosFueraDeRango(listOf(1, 2))
        assertEquals(listOf(1, 2), fuera.numeros)
    }
}