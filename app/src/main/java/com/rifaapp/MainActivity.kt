package com.rifaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rifaapp.core.designsystem.theme.RifaAppTheme
import com.rifaapp.feature.config.ConfigRifaScreen
import com.rifaapp.feature.config.ConfigRifaViewModel
import com.rifaapp.feature.rifa.TableroScreen
import com.rifaapp.feature.rifa.TableroViewModel

enum class Pantalla { TABLERO, CONFIGURACION }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as RifaApp).container

        setContent {
            RifaAppTheme {
                var pantalla by rememberSaveable { mutableStateOf(Pantalla.TABLERO) }

                BackHandler(enabled = pantalla == Pantalla.CONFIGURACION) {
                    pantalla = Pantalla.TABLERO
                }

                Scaffold(
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            TextButton(onClick = { pantalla = Pantalla.TABLERO }) {
                                Text(if (pantalla == Pantalla.TABLERO) "Tablero*" else "Tablero")
                            }
                            TextButton(onClick = { pantalla = Pantalla.CONFIGURACION }) {
                                Text(if (pantalla == Pantalla.CONFIGURACION) "Ajustes*" else "Ajustes")
                            }
                        }
                    },
                ) { padding ->
                    val contentModifier = Modifier.padding(bottom = padding.calculateBottomPadding())
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
                                onAbrirConfig = {},
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
