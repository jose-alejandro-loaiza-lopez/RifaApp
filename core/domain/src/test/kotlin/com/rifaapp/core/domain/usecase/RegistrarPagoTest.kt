package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.EstadoNumero
import com.rifaapp.core.domain.model.rifaBase
import com.rifaapp.core.domain.repository.BoletaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class RegistrarPagoTest {

    @Test
    fun `registra un pago y actualiza el estado`() = runTest {
        val repo = FakeBoletaRepository()
        val rifa = rifaBase()
        val reservar = ReservarNumero(repo)
        val resultadoReserva = reservar(rifa, 5, "Ana", "+573001234567")
        val boleta = (resultadoReserva as Resultado.Exito).valor

        val caso = RegistrarPago(repo)
        val resultado = caso(boleta.id, 4_000L)

        assertTrue(resultado is Resultado.Exito)
        val actualizada = (resultado as Resultado.Exito).valor
        assertEquals(4_000L, actualizada.montoPagado)
        assertEquals(EstadoNumero.ABONADO, actualizada.estado(rifa.precioBoleta))
    }

    @Test
    fun `rechaza montos invalidos`() = runTest {
        val repo = FakeBoletaRepository()
        val caso = RegistrarPago(repo)
        val resultado = caso(1L, 0L)
        assertEquals(ErrorDominio.MontoInvalido, (resultado as Resultado.Fallo).error)
    }

    @Test
    fun `falla si la boleta no existe`() = runTest {
        val repo = FakeBoletaRepository()
        val caso = RegistrarPago(repo)
        val resultado = caso(999L, 1_000L)
        assertEquals(ErrorDominio.BoletaNoEncontrada, (resultado as Resultado.Fallo).error)
    }
}