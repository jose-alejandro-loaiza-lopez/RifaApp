package com.rifaapp.feature.rifa

import androidx.compose.runtime.Immutable
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.EstadoNumero
import com.rifaapp.core.domain.model.Numero
import com.rifaapp.core.domain.model.Resumen
import com.rifaapp.core.domain.model.Rifa

@Immutable
data class TableroUiState(
    val cargando: Boolean = true,
    val rifa: Rifa? = null,
    val numeros: List<Numero> = emptyList(),
    val numerosVisibles: List<Numero> = emptyList(),
    val etiquetas: Map<Int, String> = emptyMap(),
    val boletas: List<Boleta> = emptyList(),
    val resumen: Resumen? = null,
    val detalle: DetalleNumero? = null,
    val busqueda: String = "",
    val estadosFiltrados: Set<EstadoNumero> = emptySet(),
    val mensaje: String? = null,
)

@Immutable
data class DetalleNumero(
    val numero: Int,
    val etiqueta: String,
    val estado: EstadoNumero,
    val boleta: Boleta?,
    val precio: Long,
) {
    val pagado: Long get() = boleta?.montoPagado ?: 0L
    val saldo: Long get() = boleta?.saldo(precio) ?: precio
}