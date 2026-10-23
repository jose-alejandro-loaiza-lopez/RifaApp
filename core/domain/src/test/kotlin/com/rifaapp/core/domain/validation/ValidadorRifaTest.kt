package com.rifaapp.core.domain.validation

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.model.rifaBase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorRifaTest {

    @Test
    fun `valida rifa correcta`() {
        val resultado = ValidadorRifa.validar(rifaBase())
        assertTrue(resultado is Resultado.Exito)
    }

    @Test
    fun `rechaza titulo vacio`() {
        val resultado = ValidadorRifa.validar(rifaBase().copy(titulo = "   "))
        assertEquals(ErrorDominio.TituloVacio, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza rango invalido`() {
        val resultado = ValidadorRifa.validar(rifaBase().copy(numeroFin = 0, numeroInicio = 5))
        assertEquals(ErrorDominio.RangoInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza precio negativo`() {
        val resultado = ValidadorRifa.validar(rifaBase().copy(precioBoleta = -1L))
        assertEquals(ErrorDominio.PrecioInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza numero inicio negativo`() {
        val resultado = ValidadorRifa.validar(rifaBase().copy(numeroInicio = -1))
        assertEquals(ErrorDominio.RangoInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza rango demasiado grande`() {
        val resultado = ValidadorRifa.validar(rifaBase().copy(numeroInicio = 0, numeroFin = 20_000))
        assertEquals(ErrorDominio.RangoInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza cantidad de digitos invalida`() {
        val resultado = ValidadorRifa.validar(rifaBase().copy(cantidadDigitos = 0))
        assertEquals(ErrorDominio.RangoInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `rechaza cantidad de digitos mayor a seis`() {
        val resultado = ValidadorRifa.validar(rifaBase().copy(cantidadDigitos = 7))
        assertEquals(ErrorDominio.RangoInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `acepta casos limite validos`() {
        assertTrue(ValidadorRifa.validar(rifaBase().copy(cantidadDigitos = 1)) is Resultado.Exito)
        assertTrue(ValidadorRifa.validar(rifaBase().copy(cantidadDigitos = 6)) is Resultado.Exito)
        assertTrue(ValidadorRifa.validar(rifaBase().copy(numeroInicio = 0, numeroFin = 0)) is Resultado.Exito)
        assertTrue(ValidadorRifa.validar(rifaBase().copy(precioBoleta = 0L)) is Resultado.Exito)
    }
}