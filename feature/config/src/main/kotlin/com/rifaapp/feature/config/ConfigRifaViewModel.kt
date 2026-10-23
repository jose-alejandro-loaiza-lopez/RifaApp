package com.rifaapp.feature.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifaapp.core.designsystem.textoVisible
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.usecase.GuardarConfiguracionRifa
import com.rifaapp.core.domain.usecase.ObservarTablero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigRifaViewModel(
    observarTablero: ObservarTablero,
    private val guardarConfiguracion: GuardarConfiguracionRifa,
) : ViewModel() {

    private val guardado = MutableStateFlow(false)
    private val guardando = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    val state: StateFlow<ConfigRifaUiState> =
        combine(observarTablero(), guardado, guardando, mensaje) { tablero, ok, guardando, texto ->
            ConfigRifaUiState(
                cargando = false,
                rifa = tablero?.rifa ?: rifaPorDefecto(),
                guardado = ok,
                guardando = guardando,
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
        if (rifaActual != null && precio == null) {
            mensaje.value = "El precio debe ser un número válido."
            return
        }
        if (precio != null && (inicio == null || fin == null)) {
            mensaje.value = "El rango debe ser numérico."
            return
        }
        if (inicio != null && fin != null && fin < inicio) {
            mensaje.value = "El fin debe ser mayor o igual que el inicio."
            return
        }

        val digitos = maxOf(
            (inicio ?: 0).toString().length,
            (fin ?: 99).toString().length,
        ).coerceAtLeast(1)

        val nueva = (rifaActual ?: rifaPorDefecto()).copy(
            titulo = formulario.titulo.trim(),
            descripcion = formulario.descripcion.trim(),
            precioBoleta = precio ?: rifaActual?.precioBoleta ?: 0L,
            fechaSorteo = formulario.fechaSorteo,
            numeroInicio = inicio ?: 0,
            numeroFin = fin ?: 99,
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

    fun consumirMensaje() {
        mensaje.value = null
    }

    fun consumirGuardado() {
        guardado.value = false
    }

    private fun rifaPorDefecto(): Rifa = Rifa(
        titulo = "Rifa del combo familiar",
        precioBoleta = 0L,
    )
}
