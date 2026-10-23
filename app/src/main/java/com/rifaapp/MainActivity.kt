package com.rifaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rifaapp.feature.rifa.TableroScreen
import com.rifaapp.feature.rifa.TableroViewModel
import com.rifaapp.ui.theme.RifaAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as RifaApp).container

        setContent {
            RifaAppTheme {
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
                )
            }
        }
    }
}
