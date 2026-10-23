package com.rifaapp.feature.rifa

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rifaapp.core.domain.model.EstadoNumero
import com.rifaapp.core.domain.model.Numero
import com.rifaapp.core.domain.model.Resumen
import com.rifaapp.core.domain.model.Rifa
import org.junit.Rule
import org.junit.Test

class TableroScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tablero_muestraNumeroDisponible() {
        val rifa = Rifa(id = 1L, titulo = "Rifa de prueba", precioBoleta = 10_000L)
        val numeros = listOf(Numero(valor = 0, estado = EstadoNumero.DISPONIBLE))

        composeTestRule.setContent {
            MaterialTheme {
                TableroScreen(
                    state = TableroUiState(
                        cargando = false,
                        rifa = rifa,
                        numeros = numeros,
                        numerosVisibles = numeros,
                        resumen = Resumen(
                            disponibles = 1,
                            reservados = 0,
                            abonados = 0,
                            pagados = 0,
                            recaudado = 0L,
                            potencial = 10_000L,
                        ),
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

        composeTestRule.onNodeWithText("00").assertIsDisplayed()
    }
}