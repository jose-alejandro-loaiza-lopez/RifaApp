package com.rifaapp.feature.config

import com.rifaapp.core.designsystem.parsearPesosACentavos
import com.rifaapp.core.domain.model.Rifa

data class ConfigRifaUiState(
    val cargando: Boolean = true,
    val rifa: Rifa? = null,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val mensaje: String? = null,
)

data class FormularioRifa(
    val titulo: String = "",
    val descripcion: String = "",
    val precioTexto: String = "",
    val fechaSorteo: java.time.LocalDate? = null,
    val numeroInicioTexto: String = "",
    val numeroFinTexto: String = "",
) {
    val precioCentavos: Long?
        get() = parsearPesosACentavos(precioTexto)

    val puedeGuardar: Boolean
        get() = titulo.isNotBlank() &&
            precioCentavos != null &&
            numeroInicioTexto.toIntOrNull() != null &&
            numeroFinTexto.toIntOrNull() != null

    companion object {
        fun desde(rifa: Rifa): FormularioRifa = FormularioRifa(
            titulo = rifa.titulo,
            descripcion = rifa.descripcion,
            precioTexto = (rifa.precioBoleta / 100).toString(),
            fechaSorteo = rifa.fechaSorteo,
            numeroInicioTexto = rifa.numeroInicio.toString(),
            numeroFinTexto = rifa.numeroFin.toString(),
        )
    }
}
