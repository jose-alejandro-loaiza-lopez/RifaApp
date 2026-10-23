package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.rifaBase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LiberarNumeroTest {

    @Test
    fun `libera un numero reservado`() = runTest {
        val repo = FakeBoletaRepository()
        val rifa = rifaBase()
        val reservar = ReservarNumero(repo)
        val boleta = (reservar(rifa, 7, "Ana", "+573001234567") as Resultado.Exito).valor

        val caso = LiberarNumero(repo)
        val resultado = caso(boleta.id)

        assertTrue(resultado is Resultado.Exito)
        assertEquals(null, repo.obtenerPorNumero(rifa.id, 7))
    }
}