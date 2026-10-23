package com.rifaapp.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rifaapp.core.database.entity.BoletaEntity
import com.rifaapp.core.database.entity.PagoEntity
import com.rifaapp.core.database.entity.RifaEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoletaDaoTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertaYRecuperaBoletaConPagos() = runBlocking {
        val rifaDao = database.rifaDao()
        val boletaDao = database.boletaDao()

        val rifaId = rifaDao.guardar(
            RifaEntity(
                titulo = "Rifa",
                precioBoletaCentavos = 10_000L,
                numeroInicio = 0,
                numeroFin = 99,
                cantidadDigitos = 2,
            )
        ).let { rifaDao.obtenerActiva()!!.id }

        val boletaId = boletaDao.insertar(
            BoletaEntity(
                rifaId = rifaId,
                numero = 7,
                clienteNombre = "Ana",
                clienteTelefono = "+573001234567",
            )
        )
        boletaDao.insertarPago(
            PagoEntity(
                boletaId = boletaId,
                monto = 4_000L,
                fecha = java.time.Instant.now(),
                nota = null,
            )
        )

        val encontrada = boletaDao.obtenerPorNumero(rifaId, 7)
        assertNotNull(encontrada)
        assertEquals(7, encontrada?.boleta?.numero)
        assertEquals(1, encontrada?.pagos?.size)
        assertEquals(4_000L, encontrada?.pagos?.first()?.monto)
    }
}