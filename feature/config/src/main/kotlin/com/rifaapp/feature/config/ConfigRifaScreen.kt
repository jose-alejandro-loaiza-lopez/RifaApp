package com.rifaapp.feature.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rifaapp.core.designsystem.parsearPesosACentavos
import com.rifaapp.core.domain.model.Rifa
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigRifaScreen(
    state: ConfigRifaUiState,
    onVolver: () -> Unit,
    onGuardar: (FormularioRifa) -> Unit,
    onRestaurar: () -> Unit,
    onConsumirMensaje: () -> Unit,
    onConsumirGuardado: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        val texto = state.mensaje ?: return@LaunchedEffect
        try {
            snackbarHostState.showSnackbar(texto)
        } finally {
            onConsumirMensaje()
        }
    }

    LaunchedEffect(state.guardado) {
        if (state.guardado) {
            onConsumirGuardado()
        }
    }

    val rifa = state.rifa
    var formulario by remember(rifa) {
        val inicial = rifa?.let { FormularioRifa.desde(it) } ?: FormularioRifa()
        mutableStateOf(inicial)
    }

    var mostrarFecha by remember { mutableStateOf(false) }
    var confirmarRestaurar by remember { mutableStateOf(false) }

    var mostrarTitulo by remember { mutableStateOf(false) }
    var tituloTmp by remember { mutableStateOf("") }
    var mostrarDescripcion by remember { mutableStateOf(false) }
    var descripcionTmp by remember { mutableStateOf("") }
    var mostrarPrecio by remember { mutableStateOf(false) }
    var precioTmp by remember { mutableStateOf("") }
    var mostrarDesde by remember { mutableStateOf(false) }
    var desdeTmp by remember { mutableStateOf("") }
    var mostrarHasta by remember { mutableStateOf(false) }
    var hastaTmp by remember { mutableStateOf("") }
    var mostrarColumnas by remember { mutableStateOf(false) }
    var columnasTmp by remember { mutableStateOf("") }

    val formularioState = androidx.compose.runtime.rememberUpdatedState(formulario)
    androidx.compose.runtime.LaunchedEffect(formulario) {
        if (!formulario.puedeGuardar) return@LaunchedEffect
        kotlinx.coroutines.delay(600)
        onGuardar(formulario)
    }
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            val actual = formularioState.value
            if (actual.puedeGuardar) onGuardar(actual)
        }
    }

    if (confirmarRestaurar) {
        AlertDialog(
            onDismissRequest = { confirmarRestaurar = false },
            properties = androidx.compose.ui.window.DialogProperties(dismissOnClickOutside = true),
            title = { Text("Restaurar por defecto") },
            text = { Text("Se eliminarán las boletas actuales y se pondrá la rifa de ejemplo otra vez.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarRestaurar = false
                    onRestaurar()
                }) { Text("Restaurar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarRestaurar = false }) { Text("Cancelar") }
            },
        )
    }

    if (mostrarFecha) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState(
                initialSelectedDateMillis = formulario.fechaSorteo?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
            )
        DatePickerDialog(
            onDismissRequest = { mostrarFecha = false },
            properties = androidx.compose.ui.window.DialogProperties(dismissOnClickOutside = true),
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = {
                        formulario = formulario.copy(fechaSorteo = null)
                        mostrarFecha = false
                    }) {
                        Text("Quitar fecha")
                    }
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            formulario = formulario.copy(
                                fechaSorteo = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate(),
                            )
                        }
                        mostrarFecha = false
                    }) {
                        Text("Aceptar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFecha = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarTitulo) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarTitulo = false },
            title = { Text("Premio (título)") },
            text = { OutlinedTextField(value = tituloTmp, onValueChange = { tituloTmp = it }, singleLine = true, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { formulario = formulario.copy(titulo = tituloTmp.trim()); mostrarTitulo = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarTitulo = false }) { Text("Cancelar") } },
        )
    }
    if (mostrarDescripcion) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarDescripcion = false },
            title = { Text("Descripción") },
            text = { OutlinedTextField(value = descripcionTmp, onValueChange = { descripcionTmp = it }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { formulario = formulario.copy(descripcion = descripcionTmp.trim()); mostrarDescripcion = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarDescripcion = false }) { Text("Cancelar") } },
        )
    }
    if (mostrarPrecio) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarPrecio = false },
            title = { Text("Precio por número (pesos)") },
            text = { OutlinedTextField(value = precioTmp, onValueChange = { precioTmp = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { formulario = formulario.copy(precioTexto = precioTmp.trim()); mostrarPrecio = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarPrecio = false }) { Text("Cancelar") } },
        )
    }
    if (mostrarDesde) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarDesde = false },
            title = { Text("Desde") },
            text = { OutlinedTextField(value = desdeTmp, onValueChange = { desdeTmp = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { formulario = formulario.copy(numeroInicioTexto = desdeTmp.trim()); mostrarDesde = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarDesde = false }) { Text("Cancelar") } },
        )
    }
    if (mostrarHasta) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarHasta = false },
            title = { Text("Hasta") },
            text = { OutlinedTextField(value = hastaTmp, onValueChange = { hastaTmp = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { formulario = formulario.copy(numeroFinTexto = hastaTmp.trim()); mostrarHasta = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarHasta = false }) { Text("Cancelar") } },
        )
    }
    if (mostrarColumnas) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarColumnas = false },
            title = { Text("Columnas") },
            text = { OutlinedTextField(value = columnasTmp, onValueChange = { columnasTmp = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { formulario = formulario.copy(columnasTexto = columnasTmp.trim()); mostrarColumnas = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarColumnas = false }) { Text("Cancelar") } },
        )
    }

Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Configuración de la rifa") },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (rifa == null) {
                Text("Cargando configuración…")
                return@Column
            }

            CampoAjuste(
                etiqueta = "Premio (título)",
                texto = if (formulario.titulo.isBlank()) "Moto pulsar 200, combo navideño…" else formulario.titulo,
                onClick = {
                    tituloTmp = formulario.titulo
                    mostrarTitulo = true
                },
            )
            Spacer(Modifier.height(12.dp))

            CampoAjuste(
                etiqueta = "Descripción (opcional)",
                texto = if (formulario.descripcion.isBlank()) "Descripción (opcional)" else formulario.descripcion,
                onClick = {
                    descripcionTmp = formulario.descripcion
                    mostrarDescripcion = true
                },
            )
            Spacer(Modifier.height(16.dp))

            CampoAjuste(
                etiqueta = "Precio por número (pesos)",
                texto = if (formulario.precioTexto.isBlank()) "$10000" else "$${formulario.precioTexto}",
                onClick = {
                    precioTmp = formulario.precioTexto
                    mostrarPrecio = true
                },
            )
            Spacer(Modifier.height(16.dp))

            CampoAjuste(
                etiqueta = "Fecha del sorteo",
                texto = formulario.fechaSorteo?.let { "Sorteo: ${it.format(FORMATO_FECHA)}" } ?: "Elegir fecha del sorteo",
                onClick = { mostrarFecha = true },
            )
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoAjuste(
                    modifier = Modifier.weight(1f),
                    etiqueta = "Desde",
                    texto = if (formulario.numeroInicioTexto.isBlank()) "Desde" else formulario.numeroInicioTexto,
                    onClick = {
                        desdeTmp = formulario.numeroInicioTexto
                        mostrarDesde = true
                    },
                )
                CampoAjuste(
                    modifier = Modifier.weight(1f),
                    etiqueta = "Hasta",
                    texto = if (formulario.numeroFinTexto.isBlank()) "Hasta" else formulario.numeroFinTexto,
                    onClick = {
                        hastaTmp = formulario.numeroFinTexto
                        mostrarHasta = true
                    },
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Rango admite de 1 a 10.000 números.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Encoger el rango deja las boletas por fuera inhabilitadas hasta liberarlas.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(12.dp))

            CampoAjuste(
                etiqueta = "Columnas de la cuadrícula",
                texto = if (formulario.columnasTexto.isBlank()) "5" else formulario.columnasTexto,
                onClick = {
                    columnasTmp = formulario.columnasTexto
                    mostrarColumnas = true
                },
            )
            Text(
                text = "Define cuántos números se muestran por fila (1 a 12).",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { confirmarRestaurar = true },
                enabled = !state.guardando,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Restaurar por defecto")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}