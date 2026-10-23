package com.rifaapp.feature.rifa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifaapp.core.designsystem.formatearPrecio
import com.rifaapp.core.designsystem.textoVisible
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
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
    private val busqueda = MutableStateFlow("")
    private val estadosFiltrados = MutableStateFlow<Set<EstadoNumero>>(emptySet())

    val state: StateFlow<TableroUiState> =
        combine(observarTablero(), seleccionado, mensaje, busqueda, estadosFiltrados) { tablero, numeroSeleccionado, texto, query, estados ->
            val rifa = tablero?.rifa
            val todosLosNumeros = tablero?.numeros.orEmpty()
            val boletas = tablero?.boletas.orEmpty()
            val boletasPorNumero = boletas.associateBy { it.numero }
            val etiquetas = todosLosNumeros.associate { numero -> numero.valor to (rifa?.formatear(numero.valor) ?: numero.valor.toString()) }

            val detalle = if (tablero != null && rifa != null && numeroSeleccionado != null) {
                DetalleNumero(
                    numero = numeroSeleccionado,
                    etiqueta = rifa.formatear(numeroSeleccionado),
                    estado = todosLosNumeros
                        .firstOrNull { it.valor == numeroSeleccionado }
                        ?.estado
                        ?: EstadoNumero.DISPONIBLE,
                    boleta = boletasPorNumero[numeroSeleccionado],
                    precio = rifa.precioBoleta,
                )
            } else {
                null
            }

            val queryLower = query.trim().lowercase()
            val numerosVisibles = todosLosNumeros.filter { numero ->
                val pasaEstado = estados.isEmpty() || numero.estado in estados
                val pasaBusqueda = if (queryLower.isEmpty()) true else {
                    val etiqueta = (rifa?.formatear(numero.valor) ?: "").lowercase()
                    if (etiqueta.contains(queryLower)) true
                    else boletasPorNumero[numero.valor]?.cliente?.let { cliente ->
                        cliente.nombre.lowercase().contains(queryLower) ||
                            cliente.telefonoE164.lowercase().contains(queryLower)
                    } ?: false
                }
                pasaEstado && pasaBusqueda
            }

            TableroUiState(
                cargando = false,
                rifa = rifa,
                numeros = todosLosNumeros,
                numerosVisibles = numerosVisibles,
                boletas = boletas,
                resumen = tablero?.resumen,
                detalle = detalle,
                busqueda = query,
                estadosFiltrados = estados,
                mensaje = texto,
            )
        }.flowOn(Dispatchers.Default).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TableroUiState(),
        )

    fun seleccionar(numero: Int) { seleccionado.value = numero }
    fun cerrarDetalle() { seleccionado.value = null }
    fun consumirMensaje() { mensaje.value = null }

    fun onBusquedaChange(texto: String) { busqueda.value = texto }
    fun toggleEstado(est: EstadoNumero) { estadosFiltrados.value = estadosFiltrados.value.toMutableSet().apply { if (this.contains(est)) remove(est) else add(est) } }
    fun limpiarFiltros() { busqueda.value = ""; estadosFiltrados.value = emptySet() }

fun reservar(nombre: String, telefono: String, montoPagoCentavos: Long? = null) {
        val rifa = state.value.rifa ?: return
        val numero = seleccionado.value ?: return

        viewModelScope.launch {
            when (val resultado = reservarNumero(rifa, numero, nombre, telefono)) {
                is Resultado.Exito -> {
                    val boleta = resultado.valor
                    val monto = montoPagoCentavos ?: 0L
                    if (monto > 0L) {
                        when (registrarPago(boleta.id, monto)) {
                            is Resultado.Exito ->
                                mensaje.value =
                                    "Número ${rifa.formatear(numero)} reservado para ${nombre.trim()} con abono de ${formatearPrecio(monto)}"
                            is Resultado.Fallo ->
                                mensaje.value =
                                    "Número ${rifa.formatear(numero)} reservado. El pago no se pudo registrar."
                        }
                    } else {
                        mensaje.value = "Número ${rifa.formatear(numero)} reservado para ${nombre.trim()}"
                    }
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

