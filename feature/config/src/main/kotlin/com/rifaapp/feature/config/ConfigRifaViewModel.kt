package com.rifaapp.feature.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifaapp.core.designsystem.textoVisible
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.repository.BoletaRepository
import com.rifaapp.core.domain.usecase.GuardarConfiguracionRifa
import com.rifaapp.core.domain.usecase.ObservarTablero
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigRifaViewModel(
    observarTablero: ObservarTablero,
    private val boletas: BoletaRepository,
    private val guardarConfiguracion: GuardarConfiguracionRifa,
) : ViewModel() {

    private val guardado = MutableStateFlow(false)
    private val guardando = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    val state: StateFlow<ConfigRifaUiState> =
        combine(observarTablero(), guardado, guardando, mensaje) { tablero, ok, g, texto ->
            ConfigRifaUiState(
                cargando = false,
                rifa = tablero?.rifa ?: rifaPorDefecto(1L),
                guardado = ok,
                guardando = g,
                mensaje = texto,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ConfigRifaUiState(),
            )

    fun guardar(formulario: FormularioRifa) {
        val rifaActual = state.value.rifa ?: return

        val precio = formulario.precioCentavos
        val inicio = formulario.numeroInicioTexto.toIntOrNull()
        val fin = formulario.numeroFinTexto.toIntOrNull()
        if (precio == null) {
            mensaje.value = "El precio debe ser un número válido."
            return
        }
        if (inicio == null || fin == null) {
            mensaje.value = "El rango debe ser numérico."
            return
        }
        if (fin < inicio) {
            mensaje.value = "El fin debe ser mayor o igual que el inicio."
            return
        }

        val digitos = maxOf(
            inicio.toString().length,
            fin.toString().length,
        ).coerceAtLeast(1)

        val nueva = rifaActual.copy(
            titulo = formulario.titulo.trim(),
            descripcion = formulario.descripcion.trim(),
            precioBoleta = precio,
            fechaSorteo = formulario.fechaSorteo,
            numeroInicio = inicio,
            numeroFin = fin,
            cantidadDigitos = digitos,
        )

        viewModelScope.launch {
            guardando.value = true
            when (val resultado = guardarConfiguracion(nueva)) {
                is Resultado.Exito -> {
                    guardado.value = true
                    mensaje.value = "Configuración guardada"
                }

                is Resultado.Fallo -> mensaje.value = resultado.error.textoVisible()
            }
            guardando.value = false
        }
    }

    fun restaurarPorDefecto() {
        val rifaActual = state.value.rifa ?: return

        viewModelScope.launch {
            guardando.value = true
            runCatching {
                boletas.obtenerPorRifa(rifaActual.id).forEach { boletas.liberar(it.id) }
            }
            val defaultRifa = Rifa(
                id = rifaActual.id,
                titulo = "Rifa del combo familiar",
                descripcion = "Rifa de ejemplo",
                fechaSorteo = LocalDate.of(2026, 12, 24),
                precioBoleta = 1_000_000L,
                numeroInicio = 0,
                numeroFin = 99,
                cantidadDigitos = 2,
            )
            when (val resultado = guardarConfiguracion(defaultRifa)) {
                is Resultado.Exito -> {
                    guardado.value = true
                    mensaje.value = "Rifa restaurada por defecto"
                }

                is Resultado.Fallo -> mensaje.value = resultado.error.textoVisible()
            }
            guardando.value = false
        }
    }

    fun consumirMensaje() {
        mensaje.value = null
    }

    fun consumirGuardado() {
        guardado.value = false
    }

    private fun rifaPorDefecto(id: Long): Rifa = Rifa(
        id = id,
        titulo = "Rifa del combo familiar",
        descripcion = "Rifa de ejemplo",
        fechaSorteo = LocalDate.of(2026, 12, 24),
        precioBoleta = 1_000_000L,
        numeroInicio = 0,
        numeroFin = 99,
        cantidadDigitos = 2,
    )
}