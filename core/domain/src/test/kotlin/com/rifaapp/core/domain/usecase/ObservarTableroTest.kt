package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.model.rifaBase
import com.rifaapp.core.domain.repository.BoletaRepository
import com.rifaapp.core.domain.repository.RifaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ObservarTableroTest {

    @Test
    fun `emite null cuando no hay rifa`() = runTest {
        val rifas = FakeRifaRepo(null)
        val boletas = FakeBoletaRepository()
        val caso = ObservarTablero(rifas, boletas)

        val resultado = caso().first()
        assertNull(resultado)
    }

    @Test
    fun `emite tablero con la rifa activa`() = runTest {
        val rifa = rifaBase()
        val rifas = FakeRifaRepo(rifa)
        val boletas = FakeBoletaRepository()
        boletas.guardar(
            com.rifaapp.core.domain.model.Boleta(
                rifaId = rifa.id,
                numero = 7,
                cliente = com.rifaapp.core.domain.model.Cliente("Ana", "+573001234567"),
            )
        )
        val caso = ObservarTablero(rifas, boletas)

        val resultado = caso().first()
        assertEquals(rifa.titulo, resultado?.rifa?.titulo)
        assertEquals(100, resultado?.numeros?.size)
    }

    private class FakeRifaRepo(inicial: Rifa?) : RifaRepository {
        private val estado = MutableStateFlow<Rifa?>(inicial)
        override fun observarActiva(): Flow<Rifa?> = estado
        override suspend fun obtenerActiva(): Rifa? = estado.value
        override suspend fun guardar(rifa: Rifa): com.rifaapp.core.domain.common.Resultado<Rifa> {
            estado.value = rifa
            return com.rifaapp.core.domain.common.Resultado.Exito(rifa)
        }
    }
}