package com.rifaapp.feature.config

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rifaapp.core.domain.model.Rifa
import org.junit.Rule
import org.junit.Test

class ConfigRifaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun config_muestraTitulo() {
        composeTestRule.setContent {
            MaterialTheme {
                ConfigRifaScreen(
                    state = ConfigRifaUiState(
                        cargando = false,
                        rifa = Rifa(
                            id = 1L,
                            titulo = "Rifa de prueba",
                            precioBoleta = 10_000L,
                        ),
                    ),
                    onVolver = {},
                    onGuardar = {},
                    onRestaurar = {},
                    onConsumirMensaje = {},
                    onConsumirGuardado = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Configuración de la rifa").assertIsDisplayed()
    }
}