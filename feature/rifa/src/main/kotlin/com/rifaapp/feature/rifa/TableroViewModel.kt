package com.rifaapp.feature.rifa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.EstadoNumero
import com.rifaapp.core.domain.usecase.LiberarNumero
import com.rifaapp.core.domain.usecase.ObservarTablero
import com.rifaapp.core.domain.usecase.RegistrarPago
import com.rifaapp.core.domain.usecase.ReservarNumero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TableroViewModel(
    private val observarTablero: ObservarTablero,
    private val reservarNumero: ReservarNumero,
    private val registrarPago: RegistrarPago,
    private val liberarNumero: LiberarNumero,
) : ViewModel() {

    private val seleccionado = MutableStateFlow<Int?>(null)
    private val mensaje = MutableStateFlow<String?>(null)

    val state: StateFlow<TableroUiState> =
        combine(observarTablero(), seleccionado, mensaje) { tablero, numeroSeleccionado, texto ->
            val rifa = tablero?.rifa

            val detalle = if (tablero != null && rifa != null && numeroSeleccionado != null) {
                DetalleNumero(
                    numero = numeroSeleccionado,
                    etiqueta = rifa.formatear(numeroSeleccionado),
                    estado = tablero.numeros
                        .firstOrNull { it.valor == numeroSeleccionado }
                        ?.estado
                        ?: EstadoNumero.DISPONIBLE,
                    boleta = tablero.boletas.firstOrNull { it.numero == numeroSeleccionado },
                    precio = rifa.precioBoleta,
                )
            } else {
                null
            }

            TableroUiState(
                cargando = false,
                rifa = rifa,
                numeros = tablero?.numeros.orEmpty(),
                resumen = tablero?.resumen,
                detalle = detalle,
                mensaje = texto,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TableroUiState(),
        )

    fun seleccionar(numero: Int) {
        seleccionado.value = numero
    }

    fun cerrarDetalle() {
        seleccionado.value = null
    }

    fun consumirMensaje() {
        mensaje.value = null
    }

    fun reservar(nombre: String, telefono: String) {
        val rifa = state.value.rifa ?: return
        val numero = seleccionado.value ?: return

        viewModelScope.launch {
            when (val resultado = reservarNumero(rifa, numero, nombre, telefono)) {
                is Resultado.Exito -> {
                    mensaje.value = "Número ${rifa.formatear(numero)} reservado para ${nombre.trim()}"
                    seleccionado.value = null
                }

                is Resultado.Fallo -> mensaje.value = resultado.error.textoVisible()
            }
        }
    }

    fun abonar(montoCentavos: Long) {
        val boletaId = state.value.detalle?.boleta?.id ?: return

        viewModelScope.launch {
            when (val resultado = registrarPago(boletaId, montoCentavos)) {
                is Resultado.Exito -> mensaje.value = "Abono de ${formatearPrecio(montoCentavos)} registrado"
                is Resultado.Fallo -> mensaje.value = resultado.error.textoVisible()
            }
        }
    }

    fun liberar() {
        val detalle = state.value.detalle ?: return
        val boletaId = detalle.boleta?.id ?: return

        viewModelScope.launch {
            when (val resultado = liberarNumero(boletaId)) {
                is Resultado.Exito -> {
                    mensaje.value = "Número ${detalle.etiqueta} liberado"
                    seleccionado.value = null
                }

                is Resultado.Fallo -> mensaje.value = resultado.error.textoVisible()
            }
        }
    }
}

fun ErrorDominio.textoVisible(): String = when (this) {
    ErrorDominio.NumeroNoDisponible -> "Ese número ya está ocupado."
    ErrorDominio.NumeroFueraDeRango -> "El número está fuera del rango de la rifa."
    ErrorDominio.NombreVacio -> "Escribe el nombre del cliente."
    ErrorDominio.TituloVacio -> "La rifa necesita un título."
    ErrorDominio.TelefonoInvalido -> "Teléfono inválido. Usa el indicativo, ej. +57 300 123 4567."
    ErrorDominio.MontoInvalido -> "El monto debe ser mayor que cero."
    ErrorDominio.PrecioInvalido -> "El precio de la boleta no es válido."
    ErrorDominio.RangoInvalido -> "El rango de números no es válido."
    ErrorDominio.RifaNoEncontrada -> "No hay una rifa configurada."
    ErrorDominio.BoletaNoEncontrada -> "No se encontró la boleta."
    is ErrorDominio.Desconocido -> mensaje
}
