package com.rifaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rifaapp.core.designsystem.theme.RifaAppTheme
import com.rifaapp.feature.config.ConfigRifaScreen
import com.rifaapp.feature.config.ConfigRifaViewModel
import com.rifaapp.feature.rifa.TableroScreen
import com.rifaapp.feature.rifa.TableroViewModel

enum class Pantalla { TABLERO, CONFIGURACION }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge deshabilitado para no dejar espacio vacío arriba/abajo.
        // try without edge to edge first
        // enableEdgeToEdge()

        val container = (application as RifaApp).container

        setContent {
            RifaAppTheme {
                var pantalla by rememberSaveable { mutableStateOf(Pantalla.TABLERO) }

                BackHandler(enabled = pantalla == Pantalla.CONFIGURACION) {
                    pantalla = Pantalla.TABLERO
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = pantalla == Pantalla.TABLERO,
                                onClick = { pantalla = Pantalla.TABLERO },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Tablero") },
                                label = { Text("Tablero") },
                            )
                            NavigationBarItem(
                                selected = pantalla == Pantalla.CONFIGURACION,
                                onClick = { pantalla = Pantalla.CONFIGURACION },
                                icon = { Icon(Icons.Filled.Settings, contentDescription = "Ajustes") },
                                label = { Text("Ajustes") },
                            )
                        }
                    },
                ) { padding ->
                    val contentModifier = Modifier.padding(bottom = 32.dp)
                    when (pantalla) {
                        Pantalla.TABLERO -> {
                            val viewModel: TableroViewModel = viewModel(factory = container.tableroViewModelFactory)
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            TableroScreen(
                                state = state,
                                modifier = contentModifier,
                                onNumeroClick = viewModel::seleccionar,
                                onCerrarDetalle = viewModel::cerrarDetalle,
                                onReservar = viewModel::reservar,
                                onAbonar = viewModel::abonar,
                                onLiberar = viewModel::liberar,
                                onConsumirMensaje = viewModel::consumirMensaje,
                                onAbrirConfig = {}, // move to bottom nav
                                onBusquedaChange = viewModel::onBusquedaChange,
                                onToggleEstado = viewModel::toggleEstado,
                                onLimpiarFiltros = viewModel::limpiarFiltros,
                            )
                        }

                        Pantalla.CONFIGURACION -> {
                            val viewModel: ConfigRifaViewModel = viewModel(factory = container.configViewModelFactory)
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            ConfigRifaScreen(
                                state = state,
                                modifier = contentModifier,
                                onVolver = { pantalla = Pantalla.TABLERO },
                                onGuardar = viewModel::guardar,
                                onRestaurar = viewModel::restaurarPorDefecto,
                                onConsumirMensaje = viewModel::consumirMensaje,
                                onConsumirGuardado = viewModel::consumirGuardado,
                            )
                        }
                    }
                }
            }
        }
    }
}
