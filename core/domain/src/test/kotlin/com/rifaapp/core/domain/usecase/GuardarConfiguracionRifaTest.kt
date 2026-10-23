package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Cliente
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.model.rifaBase
import com.rifaapp.core.domain.repository.RifaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GuardarConfiguracionRifaTest {

    @Test
    fun `guarda la configuracion cuando el rango conserva las boletas`() = runTest {
        val rifas = FakeRifaRepository(rifaBase())
        val boletas = FakeBoletaRepository()
        boletas.guardar(boleta(numero = 7))

        val nueva = rifaBase().copy(titulo = "Rifa nueva", precioBoleta = 2_000_000L)
        val resultado = GuardarConfiguracionRifa(rifas, boletas)(nueva)

        assertTrue(resultado is Resultado.Exito)
        assertEquals("Rifa nueva", rifas.estado.value?.titulo)
        assertEquals(2_000_000L, rifas.estado.value?.precioBoleta)
    }

    @Test
    fun `rechaza encoger el rango si hay boletas ocupadas por fuera`() = runTest {
        val rifas = FakeRifaRepository(rifaBase())
        val boletas = FakeBoletaRepository()
        boletas.guardar(boleta(numero = 80))
        boletas.guardar(boleta(numero = 10))

        val resultado = GuardarConfiguracionRifa(rifas, boletas)(rifaBase().copy(numeroFin = 50))

        assertEquals(
            ErrorDominio.NumerosOcupadosFueraDeRango(listOf(80)),
            (resultado as Resultado.Fallo).error,
        )
        assertEquals(99, rifas.estado.value?.numeroFin)
    }

    @Test
    fun `permite ampliar el rango sin tocar las boletas`() = runTest {
        val rifas = FakeRifaRepository(rifaBase())
        val boletas = FakeBoletaRepository()
        boletas.guardar(boleta(numero = 99))

        val resultado = GuardarConfiguracionRifa(rifas, boletas)(rifaBase().copy(numeroFin = 199))

        assertTrue(resultado is Resultado.Exito)
        assertEquals(199, rifas.estado.value?.numeroFin)
    }

    @Test
    fun `no guarda si el titulo esta vacio`() = runTest {
        val rifas = FakeRifaRepository(rifaBase())

        val resultado = GuardarConfiguracionRifa(rifas, FakeBoletaRepository())(
            rifaBase().copy(titulo = "   "),
        )

        assertEquals(ErrorDominio.TituloVacio, (resultado as Resultado.Fallo).error)
    }

    private fun boleta(numero: Int) = Boleta(
        rifaId = 1L,
        numero = numero,
        cliente = Cliente("Cliente $numero", "+573001234567"),
    )
}

private class FakeRifaRepository(inicial: Rifa) : RifaRepository {

    val estado = MutableStateFlow<Rifa?>(inicial)

    override fun observarActiva(): Flow<Rifa?> = estado

    override suspend fun obtenerActiva(): Rifa? = estado.value

    override suspend fun guardar(rifa: Rifa): Resultado<Rifa> {
        estado.value = rifa
        return Resultado.Exito(rifa)
    }
}
