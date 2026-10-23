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
import androidx.compose.foundation.text.KeyboardOptions
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
            onVolver()
        }
    }

    val rifa = state.rifa
    var formulario by remember(rifa) {
        val inicial = rifa?.let { FormularioRifa.desde(it) } ?: FormularioRifa()
        mutableStateOf(inicial)
    }

    var mostrarFecha by remember { mutableStateOf(false) }
    var confirmarRestaurar by remember { mutableStateOf(false) }

    if (confirmarRestaurar) {
        AlertDialog(
            onDismissRequest = { confirmarRestaurar = false },
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
            confirmButton = {
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
            },
            dismissButton = {
                TextButton(onClick = { mostrarFecha = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
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

            OutlinedTextField(
                value = formulario.titulo,
                onValueChange = { formulario = formulario.copy(titulo = it) },
                label = { Text("Premio (título)") },
                placeholder = { Text("Moto pulsar 200, combo navideño…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = formulario.descripcion,
                onValueChange = { formulario = formulario.copy(descripcion = it) },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = formulario.precioTexto,
                onValueChange = { formulario = formulario.copy(precioTexto = it) },
                label = { Text("Precio por número (pesos)") },
                placeholder = { Text("10000") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Los estados y saldos se recalculan automáticamente al guardar.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { mostrarFecha = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    formulario.fechaSorteo?.let { "Sorteo: ${it.format(FORMATO_FECHA)}" }
                        ?: "Elegir fecha del sorteo",
                )
            }
            formulario.fechaSorteo?.let {
                TextButton(
                    onClick = { formulario = formulario.copy(fechaSorteo = null) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Quitar fecha")
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = formulario.numeroInicioTexto,
                    onValueChange = { formulario = formulario.copy(numeroInicioTexto = it) },
                    label = { Text("Desde") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = formulario.numeroFinTexto,
                    onValueChange = { formulario = formulario.copy(numeroFinTexto = it) },
                    label = { Text("Hasta") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
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

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onGuardar(formulario) },
                enabled = formulario.puedeGuardar && !state.guardando,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.guardando) "Guardando…" else "Guardar cambios")
            }

            Spacer(Modifier.height(8.dp))

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { confirmarRestaurar = true },
                enabled = !state.guardando,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Restaurar por defecto")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cancelar")
            }

        }
    }
}
