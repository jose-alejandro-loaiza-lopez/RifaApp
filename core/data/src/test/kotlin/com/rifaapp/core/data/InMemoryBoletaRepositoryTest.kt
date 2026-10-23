package com.rifaapp.core.data

import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Cliente
import com.rifaapp.core.domain.model.Pago
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class InMemoryBoletaRepositoryTest {

    @Test
    fun `guarda y recupera boletas por rifa`() = runTest {
        val repo = InMemoryBoletaRepository(emptyList())
        val boleta = Boleta(rifaId = 1L, numero = 7, cliente = Cliente("Ana", "+573001234567"))

        val resultado = repo.guardar(boleta)
        assertTrue(resultado is Resultado.Exito)

        val lista = repo.obtenerPorRifa(1L)
        assertEquals(1, lista.size)
        assertEquals(7, lista.first().numero)
    }

    @Test
    fun `inicializa con boletas existentes`() = runTest {
        val inicial = Boleta(id = 5L, rifaId = 1L, numero = 1, cliente = Cliente("Ana", "+573001234567"), pagos = listOf(Pago(id = 9L, boletaId = 5L, monto = 1L, fecha = Instant.now())))
        val repo = InMemoryBoletaRepository(listOf(inicial))
        assertEquals(1, repo.obtenerPorRifa(1L).size)
    }

    @Test
    fun `inicializa con boleta sin pagos`() = runTest {
        val inicial = Boleta(id = 5L, rifaId = 1L, numero = 1, cliente = Cliente("Ana", "+573001234567"))
        val repo = InMemoryBoletaRepository(listOf(inicial))
        assertEquals(1, repo.obtenerPorRifa(1L).size)
    }

    @Test
    fun `registra pagos y actualiza el monto`() = runTest {
        val repo = InMemoryBoletaRepository(emptyList())
        val boleta = repo.guardar(Boleta(rifaId = 1L, numero = 7, cliente = Cliente("Ana", "+573001234567"))) as Resultado.Exito
        val id = boleta.valor.id

        repo.registrarPago(id, 5_000L, Instant.now(), null)
        val actualizada = repo.obtenerPorNumero(1L, 7)

        assertEquals(5_000L, actualizada?.montoPagado)
    }

    @Test
    fun `observa por rifa`() = runTest {
        val repo = InMemoryBoletaRepository(emptyList())
        repo.guardar(Boleta(rifaId = 1L, numero = 7, cliente = Cliente("Ana", "+573001234567")))
        val lista = repo.observarPorRifa(1L).first()
        assertEquals(1, lista.size)
    }

    @Test
    fun `actualiza monto pagado`() = runTest {
        val repo = InMemoryBoletaRepository(emptyList())
        val boleta = repo.guardar(Boleta(rifaId = 1L, numero = 7, cliente = Cliente("Ana", "+573001234567"))) as Resultado.Exito
        val id = boleta.valor.id

        val resultado = repo.actualizarMontoPagado(id, 3_000L)
        assertTrue(resultado is Resultado.Exito)
        assertEquals(3_000L, (resultado as Resultado.Exito).valor.montoPagado)
    }

    @Test
    fun `libera una boleta`() = runTest {
        val repo = InMemoryBoletaRepository(emptyList())
        val boleta = repo.guardar(Boleta(rifaId = 1L, numero = 7, cliente = Cliente("Ana", "+573001234567"))) as Resultado.Exito
        val id = boleta.valor.id

        val resultado = repo.liberar(id)
        assertTrue(resultado is Resultado.Exito)
        assertEquals(0, repo.obtenerPorRifa(1L).size)
    }

    @Test
    fun `falla al registrar pago en boleta inexistente`() = runTest {
        val repo = InMemoryBoletaRepository(emptyList())
        val resultado = repo.registrarPago(999L, 1_000L, Instant.now(), null)
        assertTrue(resultado is Resultado.Fallo)
    }

    @Test
    fun `falla al actualizar monto en boleta inexistente`() = runTest {
        val repo = InMemoryBoletaRepository(emptyList())
        val resultado = repo.actualizarMontoPagado(999L, 1_000L)
        assertTrue(resultado is Resultado.Fallo)
    }
}