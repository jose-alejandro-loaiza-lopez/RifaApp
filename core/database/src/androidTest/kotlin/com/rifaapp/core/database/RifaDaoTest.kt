package com.rifaapp.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rifaapp.core.database.entity.RifaEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RifaDaoTest {

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
    fun guardarYObservarRifa() = runBlocking {
        val dao = database.rifaDao()
        dao.guardar(
            RifaEntity(
                titulo = "Rifa de prueba",
                precioBoletaCentavos = 10_000L,
                numeroInicio = 0,
                numeroFin = 99,
                cantidadDigitos = 2,
                columnas = 5,
            )
        )

        val rifa = dao.obtenerActiva()
        assertNotNull(rifa)
        assertEquals("Rifa de prueba", rifa?.titulo)
        assertEquals(10_000L, rifa?.precioBoletaCentavos)
    }
}