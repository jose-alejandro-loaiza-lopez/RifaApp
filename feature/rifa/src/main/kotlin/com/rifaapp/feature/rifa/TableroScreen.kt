package com.rifaapp.feature.rifa

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalContext


import androidx.compose.material3.FilterChip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.designsystem.formatearPrecio
import com.rifaapp.core.designsystem.parsearPesosACentavos
import com.rifaapp.core.domain.model.EstadoNumero
import com.rifaapp.core.domain.model.Numero
import com.rifaapp.core.domain.model.Resumen
import com.rifaapp.core.domain.model.Rifa
import java.time.format.DateTimeFormatter

private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableroScreen(
    state: TableroUiState,
    onNumeroClick: (Int) -> Unit,
    onCerrarDetalle: () -> Unit,
    onReservar: (String, String, Long?) -> Unit,
    onAbonar: (Long) -> Unit,
    onLiberar: () -> Unit,
    onConsumirMensaje: () -> Unit,
    onAbrirConfig: () -> Unit,
    onBusquedaChange: (String) -> Unit,
    onToggleEstado: (EstadoNumero) -> Unit,
    onLimpiarFiltros: () -> Unit,
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

    var mostrarFiltros by remember { mutableStateOf(false) }

    val detalleSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filtrosSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.rifa?.titulo ?: "RifaApp",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        state.rifa?.let { rifa ->
                            val fecha = rifa.fechaSorteo?.let { " · sorteo ${it.format(FORMATO_FECHA)}" }.orEmpty()
                            Text(
                                text = "${formatearPrecio(rifa.precioBoleta)} por número$fecha",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFiltros = true }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Filtrar números")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val rifa = state.rifa

        when {
            state.cargando -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            rifa == null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("No hay una rifa configurada todavía.")
            }

            else -> Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
                state.resumen?.let { ResumenRifa(it) }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                    items = state.numerosVisibles,
                    key = { it.valor },
                    contentType = { "numero" },
                ) { numero ->
                        val onClick = remember(numero.valor, onNumeroClick) { { onNumeroClick(numero.valor) } }
                        CeldaNumero(
                            etiqueta = state.etiquetas[numero.valor] ?: rifa.formatear(numero.valor),
                            estado = numero.estado,
                            onClick = onClick,
                        )
                    }
                }
            }
        }
    }

    val rifa = state.rifa
    val detalle = state.detalle
    if (rifa != null && detalle != null) {
        ModalBottomSheet(onDismissRequest = onCerrarDetalle, sheetState = detalleSheetState) {
            DetalleContenido(
                rifa = rifa,
                detalle = detalle,
                onReservar = onReservar,
                onAbonar = onAbonar,
                onLiberar = onLiberar,
            )
        }
    }

    if (mostrarFiltros) {
        ModalBottomSheet(onDismissRequest = { mostrarFiltros = false }, sheetState = filtrosSheetState) {
            FiltrosTablero(
                busqueda = state.busqueda,
                estadosFiltrados = state.estadosFiltrados,
                onBusquedaChange = onBusquedaChange,
                onToggleEstado = onToggleEstado,
                onLimpiarFiltros = onLimpiarFiltros,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResumenRifa(resumen: Resumen) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Contador(
                etiqueta = "Disponibles",
                cantidad = resumen.disponibles,
                fondo = MaterialTheme.colorScheme.surfaceVariant,
                texto = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Contador(
                etiqueta = "Reservados",
                cantidad = resumen.reservados,
                fondo = MaterialTheme.colorScheme.tertiaryContainer,
                texto = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f),
            )
            Contador(
                etiqueta = "Abonados",
                cantidad = resumen.abonados,
                fondo = MaterialTheme.colorScheme.secondaryContainer,
                texto = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f),
            )
            Contador(
                etiqueta = "Pagados",
                cantidad = resumen.pagados,
                fondo = MaterialTheme.colorScheme.primary,
                texto = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Recaudado ${formatearPrecio(resumen.recaudado)} de ${formatearPrecio(resumen.potencial)}",
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun Contador(
    etiqueta: String,
    cantidad: Int,
    fondo: Color,
    texto: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(fondo)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = cantidad.toString(),
            color = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = etiqueta,
            color = texto,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CeldaNumero(
    etiqueta: String,
    estado: EstadoNumero,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val colorPair = remember(estado, colorScheme) { coloresEstadoSincrono(estado, colorScheme) }
    val fondo = colorPair.first
    val texto = colorPair.second

    Box(
        modifier = Modifier
            .semantics { contentDescription = "$etiqueta, ${estado.nombreLegible()}" }
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(fondo)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = etiqueta,
            color = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun coloresEstadoSincrono(
    estado: EstadoNumero,
    scheme: androidx.compose.material3.ColorScheme,
): Pair<Color, Color> = when (estado) {
    EstadoNumero.DISPONIBLE -> scheme.surfaceVariant to scheme.onSurfaceVariant
    EstadoNumero.RESERVADO -> scheme.tertiaryContainer to scheme.onTertiaryContainer
    EstadoNumero.ABONADO -> scheme.secondaryContainer to scheme.onSecondaryContainer
    EstadoNumero.PAGADO -> scheme.primary to scheme.onPrimary
}

@Composable
private fun EtiquetaEstado(estado: EstadoNumero) {
    val (fondo, texto) = coloresEstadoSincrono(estado, MaterialTheme.colorScheme)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = estado.nombreLegible(),
            color = texto,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

private fun EstadoNumero.nombreLegible(): String = when (this) {
    EstadoNumero.DISPONIBLE -> "Disponible"
    EstadoNumero.RESERVADO -> "Reservado"
    EstadoNumero.ABONADO -> "Abonado"
    EstadoNumero.PAGADO -> "Pagado"
}

@Composable
private fun DetalleContenido(
    rifa: Rifa,
    detalle: DetalleNumero,
    onReservar: (String, String, Long?) -> Unit,
    onAbonar: (Long) -> Unit,
    onLiberar: () -> Unit,
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var montoPago by remember { mutableStateOf("") }
    var mostrarDialogoAbono by remember { mutableStateOf(false) }
    var mostrarDialogoLiberar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val boleta = detalle.boleta

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Número ${detalle.etiqueta}",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.width(12.dp))
            EtiquetaEstado(detalle.estado)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (boleta == null) {
            Text("Reservar este número", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del cliente") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Celular con indicativo") },
                placeholder = { Text("+57 300 123 4567") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = montoPago,
                onValueChange = { montoPago = it },
                label = { Text("Abono inicial (opcional)") },
                placeholder = { Text("0") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onReservar(nombre, telefono, parsearPesosACentavos(montoPago)) },
                enabled = nombre.isNotBlank() && telefono.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Reservar número")
            }
        } else {
            InfoFila("Cliente", boleta.cliente.nombre)
            InfoFila("Celular", boleta.cliente.telefonoE164)
            InfoFila("Precio", formatearPrecio(detalle.precio))
            InfoFila("Pagado", formatearPrecio(detalle.pagado))
            InfoFila("Saldo", formatearPrecio(detalle.saldo))

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (detalle.saldo > 0L) {
                    Button(
                        onClick = { mostrarDialogoAbono = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Registrar abono")
                    }
                }
                OutlinedButton(
                    onClick = {
                        abrirWhatsApp(
                            context = context,
                            telefonoE164 = boleta.cliente.telefonoE164,
                            mensaje = mensajeWhatsApp(rifa, detalle, boleta),
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("WhatsApp")
                }
            }

            TextButton(
                onClick = { mostrarDialogoLiberar = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Liberar número")
            }
        }
    }

    if (mostrarDialogoAbono) {
        DialogoMonto(
            saldo = detalle.saldo,
            onConfirmar = { monto ->
                mostrarDialogoAbono = false
                onAbonar(monto)
            },
            onCancelar = { mostrarDialogoAbono = false },
        )
    }

    if (mostrarDialogoLiberar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLiberar = false },
            title = { Text("¿Liberar el número?") },
            text = { Text("La boleta se elimina y el número vuelve a estar disponible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoLiberar = false
                        onLiberar()
                    },
                ) {
                    Text("Liberar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLiberar = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun InfoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun DialogoMonto(
    saldo: Long,
    onConfirmar: (Long) -> Unit,
    onCancelar: () -> Unit,
) {
    var texto by remember { mutableStateOf("") }
    val centavos = parsearPesosACentavos(texto) ?: 0L

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar abono") },
        text = {
            Column {
                Text(
                    text = "Saldo pendiente: ${formatearPrecio(saldo)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Monto recibido") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(centavos) },
                enabled = centavos > 0L,
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        },
    )
}

private fun mensajeWhatsApp(rifa: Rifa, detalle: DetalleNumero, boleta: Boleta): String = buildString {
    append("Hola ${boleta.cliente.nombre}! Te escribo por la rifa \"${rifa.titulo}\". ")
    append("Tienes el número ${detalle.etiqueta}. ")
    if (detalle.saldo > 0L) {
        append("Saldo pendiente: ${formatearPrecio(detalle.saldo)}. ")
    } else {
        append("Está pagado por completo. ")
    }
    rifa.fechaSorteo?.let { append("El sorteo es el ${it.format(FORMATO_FECHA)}. ") }
    append("¡Gracias!")
}

private fun abrirWhatsApp(context: Context, telefonoE164: String, mensaje: String) {
    val numero = telefonoE164.removePrefix("+")
    val uri = Uri.parse("https://wa.me/$numero?text=${Uri.encode(mensaje)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    runCatching { context.startActivity(intent) }
}

@Composable
private fun FiltrosTablero(
    busqueda: String,
    estadosFiltrados: Set<EstadoNumero>,
    onBusquedaChange: (String) -> Unit,
    onToggleEstado: (EstadoNumero) -> Unit,
    onLimpiarFiltros: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            label = { Text("Buscar (número, nombre o celular)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(EstadoNumero.DISPONIBLE, EstadoNumero.RESERVADO, EstadoNumero.ABONADO, EstadoNumero.PAGADO)
                .forEach { estado ->
                    FilterChip(
                        selected = estado in estadosFiltrados,
                        onClick = { onToggleEstado(estado) },
                        label = { Text(estado.nombreLegible()) },
                    )
                }
            if (busqueda.isNotBlank() || estadosFiltrados.isNotEmpty()) {
                FilterChip(
                    selected = false,
                    onClick = onLimpiarFiltros,
                    label = { Text("Limpiar filtros") },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TableroPreview() {
    val numeros = (0 until 100).map { valor ->
        Numero(
            valor = valor,
            estado = when {
                valor % 7 == 0 -> EstadoNumero.PAGADO
                valor % 5 == 0 -> EstadoNumero.ABONADO
                valor % 3 == 0 -> EstadoNumero.RESERVADO
                else -> EstadoNumero.DISPONIBLE
            },
        )
    }
    val resumen = Resumen(
        disponibles = numeros.count { it.estado == EstadoNumero.DISPONIBLE },
        reservados = numeros.count { it.estado == EstadoNumero.RESERVADO },
        abonados = numeros.count { it.estado == EstadoNumero.ABONADO },
        pagados = numeros.count { it.estado == EstadoNumero.PAGADO },
        recaudado = 6_500_000L,
        potencial = 100_000_000L,
    )

    MaterialTheme {
        TableroScreen(
            state = TableroUiState(
                cargando = false,
                rifa = Rifa(id = 1L, titulo = "Rifa de prueba", precioBoleta = 1_000_000L),
                numeros = numeros,
                resumen = resumen,
            ),
            onNumeroClick = {},
            onCerrarDetalle = {},
            onReservar = { _, _, _ -> },
            onAbonar = {},
            onLiberar = {},
            onConsumirMensaje = {},
            onAbrirConfig = {},
            onBusquedaChange = {},
            onToggleEstado = {},
            onLimpiarFiltros = {},
        )
    }
}
