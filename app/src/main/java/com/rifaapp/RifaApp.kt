package com.rifaapp

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import com.rifaapp.core.database.RoomBoletaRepository
import com.rifaapp.core.database.RoomRifaRepository
import com.rifaapp.core.database.AppDatabase
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Cliente
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.usecase.GuardarConfiguracionRifa
import com.rifaapp.core.domain.usecase.LiberarNumero
import com.rifaapp.core.domain.usecase.ObservarTablero
import com.rifaapp.core.domain.usecase.RegistrarPago
import com.rifaapp.core.domain.usecase.ReservarNumero
import com.rifaapp.feature.config.ConfigRifaViewModel
import com.rifaapp.feature.rifa.TableroViewModel
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.runBlocking

class RifaApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(application: Application) {

    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "rifa.db",
    ).fallbackToDestructiveMigration().build()

    private val rifas = RoomRifaRepository(database.rifaDao())
    private val boletas = RoomBoletaRepository(database.boletaDao())

    init {
        runBlocking {
            if (rifas.obtenerActiva() == null) {
                rifas.guardar(
                    Rifa(
                        titulo = "Rifa del combo familiar",
                        fechaSorteo = LocalDate.of(2026, 12, 24),
                        precioBoleta = 1_000_000L,
                    )
                )
                val rifaId = rifas.obtenerActiva()?.id ?: 1L
                listOf(
                    Triple(7, "Ana Torres", 0L),
                    Triple(12, "Luis Pérez", 400_000L),
                    Triple(23, "Marta Gómez", 1_000_000L),
                ).forEach { (numero, nombre, pagado) ->
                    val id = (boletas.guardar(
                        Boleta(
                            rifaId = rifaId,
                            numero = numero,
                            cliente = Cliente(nombre, "+57300${100_000_000 + numero}"),
                        )
                    ) as Resultado.Exito<Boleta>).valor.id
                    if (pagado > 0L) {
                        boletas.registrarPago(id, pagado, Instant.now(), null)
                    }
                }
            }
        }
    }

    private val observarTablero = ObservarTablero(rifas, boletas)
    private val reservarNumero = ReservarNumero(boletas)
    private val registrarPago = RegistrarPago(boletas)
    private val liberarNumero = LiberarNumero(boletas)
    private val guardarConfiguracion = GuardarConfiguracionRifa(rifas, boletas)

    val tableroViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            TableroViewModel(
                observarTablero = observarTablero,
                reservarNumero = reservarNumero,
                registrarPago = registrarPago,
                liberarNumero = liberarNumero,
            )
        }
    }

    val configViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            ConfigRifaViewModel(observarTablero, guardarConfiguracion)
        }
    }
}
