package com.rifaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
        enableEdgeToEdge()

        val container = (application as RifaApp).container

        setContent {
            RifaAppTheme {
                var pantalla by rememberSaveable { mutableStateOf(Pantalla.TABLERO) }

                BackHandler(enabled = pantalla == Pantalla.CONFIGURACION) {
                    pantalla = Pantalla.TABLERO
                }

                when (pantalla) {
                    Pantalla.TABLERO -> {
                        val viewModel: TableroViewModel = viewModel(factory = container.tableroViewModelFactory)
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        TableroScreen(
                            state = state,
                            onNumeroClick = viewModel::seleccionar,
                            onCerrarDetalle = viewModel::cerrarDetalle,
                            onReservar = viewModel::reservar,
                            onAbonar = viewModel::abonar,
                            onLiberar = viewModel::liberar,
                            onConsumirMensaje = viewModel::consumirMensaje,
                            onAbrirConfig = { pantalla = Pantalla.CONFIGURACION },
                        )
                    }

                    Pantalla.CONFIGURACION -> {
                        val viewModel: ConfigRifaViewModel = viewModel(factory = container.configViewModelFactory)
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        ConfigRifaScreen(
                            state = state,
                            onVolver = { pantalla = Pantalla.TABLERO },
                            onGuardar = viewModel::guardar,
                            onConsumirMensaje = viewModel::consumirMensaje,
                            onConsumirGuardado = viewModel::consumirGuardado,
                        )
                    }
                }
            }
        }
    }
}
