package com.rifaapp

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rifaapp.core.data.InMemoryBoletaRepository
import com.rifaapp.core.data.InMemoryRifaRepository
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Cliente
import com.rifaapp.core.domain.model.Pago
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

class RifaApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}

/**
 * Inyección de dependencias manual: el único punto que conoce las implementaciones.
 * Cuando entre Room, solo cambia esta clase.
 */
class AppContainer {

    private val rifas = InMemoryRifaRepository(
        rifaInicial = Rifa(
            id = 1L,
            titulo = "Rifa del combo familiar",
            descripcion = "Rifa de ejemplo",
            fechaSorteo = LocalDate.of(2026, 12, 24),
            precioBoleta = 1_000_000L,
        ),
    )

    private val boletas = InMemoryBoletaRepository(
        boletasIniciales = listOf(
            Boleta(
                id = 1L,
                rifaId = 1L,
                numero = 7,
                cliente = Cliente("Ana Torres", "+573001112233"),
            ),
            Boleta(
                id = 2L,
                rifaId = 1L,
                numero = 12,
                cliente = Cliente("Luis Pérez", "+573004445566"),
                pagos = listOf(
                    Pago(id = 1L, boletaId = 2L, monto = 400_000L, fecha = Instant.now()),
                ),
            ),
            Boleta(
                id = 3L,
                rifaId = 1L,
                numero = 23,
                cliente = Cliente("Marta Gómez", "+573007778899"),
                pagos = listOf(
                    Pago(id = 2L, boletaId = 3L, monto = 1_000_000L, fecha = Instant.now()),
                ),
            ),
        ),
    )

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
