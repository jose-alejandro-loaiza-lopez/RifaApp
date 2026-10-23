package com.rifaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import com.rifaapp.core.designsystem.theme.RifaAppTheme
import com.rifaapp.core.designsystem.theme.ThemeColor
import com.rifaapp.core.designsystem.theme.ThemeMode
import com.rifaapp.feature.config.ConfigRifaScreen
import com.rifaapp.feature.config.ConfigRifaViewModel
import com.rifaapp.feature.rifa.TableroScreen
import com.rifaapp.feature.rifa.TableroViewModel

enum class Pantalla { TABLERO, CONFIGURACION }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = this.application as RifaApp

        setContent {
            val themeMode by application.themePreferences.mode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.AUTO,
            )
            val themeColor by application.themePreferences.color.collectAsStateWithLifecycle(
                initialValue = ThemeColor.AUTO,
            )
            val darkTheme = when (themeMode) {
                ThemeMode.AUTO -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            RifaAppTheme(darkTheme = darkTheme, themeColor = themeColor) {
                val container by application.containerFlow.collectAsStateWithLifecycle(
                    initialValue = null,
                )
                val readyContainer = container

                if (readyContainer == null) {
                    BoxLoading()
                    return@RifaAppTheme
                }

                MainContainer(
                    container = readyContainer,
                    themeMode = themeMode,
                    themeColor = themeColor,
                    onThemeModeChange = { application.themePreferences.setMode(it) },
                    onThemeColorChange = { application.themePreferences.setColor(it) },
                )
            }
        }
    }
}

@Composable
private fun BoxLoading() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MainContainer(
    container: AppContainer,
    themeMode: ThemeMode,
    themeColor: ThemeColor,
    onThemeModeChange: (ThemeMode) -> Unit,
    onThemeColorChange: (ThemeColor) -> Unit,
) {
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.TABLERO) }

    BackHandler(enabled = pantalla == Pantalla.CONFIGURACION) {
        pantalla = Pantalla.TABLERO
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.padding(bottom = 0.dp),
                tonalElevation = 6.dp,
            ) {
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
                    onEditar = viewModel::editar,
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
                    themeMode = themeMode,
                    themeColor = themeColor,
                    onThemeModeChange = onThemeModeChange,
                    onThemeColorChange = onThemeColorChange,
                    modifier = contentModifier,
                    onVolver = { pantalla = Pantalla.TABLERO },
                    onGenerarRespaldo = { viewModel.generarRespaldoJson() },
                    onImportarRespaldo = { json -> viewModel.importarRespaldoJson(json) },
                    onGuardar = viewModel::guardar,
                    onRestaurar = viewModel::restaurarPorDefecto,
                    onConsumirMensaje = viewModel::consumirMensaje,
                    onConsumirGuardado = viewModel::consumirGuardado,
                )
            }
        }
    }
}
