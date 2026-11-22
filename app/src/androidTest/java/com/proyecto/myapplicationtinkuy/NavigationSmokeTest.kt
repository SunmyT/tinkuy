package com.proyecto.myapplicationtinkuy

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationSmokeTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun searchMatchesNamesAndRegionsAcrossTheCatalog() {
        enterMarketplace()
        compose.onNodeWithContentDescription("Buscar productos").performTextInput("ceramica piura")
        compose.onNodeWithContentDescription("Buscar productos").performImeAction()
        compose.onNodeWithText("Cerámica de Chulucanas").assertIsDisplayed()
        compose.onNodeWithText("Tejido de Alpaca").assertDoesNotExist()

        compose.onNodeWithText("Textiles").performClick().assertIsSelected()
        compose.onNodeWithText("No encontramos productos").assertIsDisplayed()
        compose.onNodeWithContentDescription("Borrar búsqueda").performClick()
        compose.onNodeWithText("Tejido de Alpaca").assertIsDisplayed()

        compose.onNodeWithContentDescription("Catálogo").performClick()
        compose.onNodeWithContentDescription("Buscar productos").performTextInput("cafe")
        compose.onNodeWithContentDescription("Buscar productos").performImeAction()
        compose.onNodeWithText("Café peruano en grano").assertIsDisplayed()
        compose.onNodeWithText("Cerámica de Chulucanas").assertDoesNotExist()
    }

    @Test
    fun ordersFilterByStatusAndShowTheSelectedOrder() {
        enterMarketplace()
        compose.onNodeWithContentDescription("Perfil").performClick()
        compose.onNodeWithText("Mis pedidos").performScrollTo().performClick()

        compose.onNodeWithText("Pendientes").performClick().assertIsSelected()
        compose.onNodeWithText("Pedido #156156").assertIsDisplayed()
        compose.onNodeWithText("Pedido #156155").assertDoesNotExist()

        compose.onNodeWithText("Entregados").performClick().assertIsSelected()
        compose.onNodeWithText("Pedido #156155").assertIsDisplayed()
        compose.onNodeWithText("Pedido #156156").assertDoesNotExist()

        compose.onNodeWithText("En curso").performClick().assertIsSelected()
        compose.onNodeWithText("Pedido #156154").performClick()
        compose.onNodeWithText("Seguimiento").assertIsDisplayed()
        compose.onNodeWithText("Tejido de Oveja").assertExists()

        pressBack()
        compose.onNodeWithText("En curso").assertIsSelected()
        compose.onNodeWithText("Todos").performClick().assertIsSelected()
        compose.onNodeWithText("Pedido #156156").assertExists()
        compose.onNodeWithText("Pedido #156155").assertExists()
    }

    @Test
    fun notificationPreferenceSurvivesActivityRecreation() {
        enterMarketplace()
        compose.onNodeWithContentDescription("Notificaciones").performClick()
        val preference = compose.onNodeWithText("Cambios en mis pedidos")
        val initial = preference.fetchSemanticsNode().config[SemanticsProperties.ToggleableState]
        val updated = if (initial == ToggleableState.On) ToggleableState.Off else ToggleableState.On
        preference.performClick()
        compose.activityRule.scenario.recreate()
        preference.assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, updated))
        preference.performClick()
    }

    @Test
    fun homeFiltersProductsAndOpensTheSelectedProduct() {
        enterMarketplace()

        compose.onNodeWithText("Textiles").performScrollTo().performClick()
        compose.onNodeWithText("Tejido de Alpaca").assertIsDisplayed()
        compose.onNodeWithText("Tejido de Oveja").assertIsDisplayed()
        compose.onNodeWithText("Cerámica de Chulucanas").assertDoesNotExist()

        compose.onNodeWithText("Cerámica").performScrollTo().performClick()
        compose.onNodeWithText("Cerámica de Chulucanas").assertIsDisplayed()
        compose.onNodeWithText("Tejido de Alpaca").assertDoesNotExist()

        compose.onNodeWithText("Alimentos").performScrollTo().performClick()
        compose.onNodeWithText("Café peruano en grano").assertIsDisplayed()
        compose.onNodeWithText("Cerámica de Chulucanas").assertDoesNotExist()

        compose.onNodeWithText("Todos").performScrollTo().performClick()
        compose.onNodeWithText("Tejido de Alpaca").assertExists()
        compose.onNodeWithText("Café peruano en grano").assertExists()
        compose.onNodeWithText("Cerámica de Chulucanas").performScrollTo().performClick()
        compose.onNodeWithText("Cerámica de Chulucanas").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Cotizar").assertExists()

        pressBack()
        compose.onNodeWithText("Productos destacados").assertIsDisplayed()
    }

    @Test
    fun catalogOpensProductDetailAndQuotation() {
        enterMarketplace()
        compose.onNodeWithContentDescription("Catálogo").performClick()
        compose.onAllNodesWithText("Tejido de Alpaca")[0].performClick()
        compose.onNodeWithText("Disponible: 20 unidades").performScrollTo().assertIsDisplayed()

        compose.onNodeWithText("Cotizar").performScrollTo().performClick()
        compose.onNodeWithText("Solicitar cotización").assertIsDisplayed()
        compose.onNodeWithText("Enviar Solicitud").performScrollTo().performClick()
        compose.onNodeWithText("Solicitud enviada").assertIsDisplayed()
    }

    @Test
    fun profileOpensAccount() {
        enterMarketplace()
        compose.onNodeWithContentDescription("Perfil").performClick()
        compose.onNodeWithText("Mi cuenta").performClick()
        compose.onNodeWithText("Datos personales").assertIsDisplayed()
    }

    @Test
    fun settingsOpensSyncAndAbout() {
        enterMarketplace()
        openSettings()
        compose.onNodeWithText("Sincronización").performClick()
        compose.onNodeWithText("Información guardada").assertIsDisplayed()

        openSettings()
        compose.onNodeWithText("Acerca de Tinkuy").performClick()
        compose.onNodeWithText("Modo sin conexión").assertIsDisplayed()
    }

    @Test
    fun registrationOpensPasswordAndVerification() {
        compose.onNodeWithText("Crear Cuenta").performClick()
        compose.onNodeWithText("Siguiente").performClick()
        compose.onNodeWithText("Crea tu contraseña").assertIsDisplayed()
        compose.onNodeWithText("Crear Cuenta").performClick()
        compose.onNodeWithText("Verifica tu número").assertIsDisplayed()
    }

    private fun enterMarketplace() {
        compose.onNodeWithText("Iniciar Sesión").performClick()
        compose.onNodeWithText("Ingresar").performClick()
        compose.onNodeWithText("Buenas tardes").assertIsDisplayed()
    }

    private fun openSettings() {
        compose.onNodeWithContentDescription("Perfil").performClick()
        compose.onNodeWithText("Configuración").performClick()
    }
}
