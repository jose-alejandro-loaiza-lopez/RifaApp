package com.rifaapp.core.data

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class InMemoryRifaRepositoryTest {

    @Test
    fun `guarda y observa la rifa activa`() = runTest {
        val inicial = Rifa(id = 1L, titulo = "Inicial", precioBoleta = 10_000L, numeroInicio = 0, numeroFin = 99, cantidadDigitos = 2)
        val repo = InMemoryRifaRepository(inicial)

        val guardada = Rifa(id = 1L, titulo = "Nueva", precioBoleta = 20_000L, numeroInicio = 0, numeroFin = 99, cantidadDigitos = 2)
        val resultado = repo.guardar(guardada)
        assertEquals(Resultado.Exito(guardada), resultado)

        val activa = repo.observarActiva().first()
        assertNotNull(activa)
        assertEquals("Nueva", activa?.titulo)
        assertEquals("Nueva", repo.obtenerActiva()?.titulo)
    }
}