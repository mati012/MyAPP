package com.example.myapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginScreen_elementsDisplayed() {
        // Verifica que el TextField de "Correo electrónico" se muestre en pantalla
        composeTestRule.onNodeWithText("Correo electrónico").assertExists()
        // Verifica que el TextField de "Contraseña" se muestre en pantalla
        composeTestRule.onNodeWithText("Contraseña").assertExists()
        // Verifica que el botón "Iniciar Sesión" esté visible
        composeTestRule.onNodeWithText("Iniciar Sesión").assertExists()
        // Verifica que existan los botones "Registrarse" y "¿Olvidaste tu contraseña?"
        composeTestRule.onNodeWithText("Registrarse").assertExists()
        composeTestRule.onNodeWithText("¿Olvidaste tu contraseña?").assertExists()
    }

    @Test
    fun loginScreen_textInput() {
        val testEmail = "test@example.com"
        val testPassword = "123456"

        // Ingresa el correo electrónico
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput(testEmail)
        // Ingresa la contraseña
        composeTestRule.onNodeWithText("Contraseña").performTextInput(testPassword)

        // Verifica que los textos ingresados estén en la pantalla
        composeTestRule.onNodeWithText(testEmail).assertExists()
        composeTestRule.onNodeWithText(testPassword).assertExists()
    }

    @Test
    fun loginScreen_invalidCredentialsShowsError() {
        // Ingresa datos incorrectos
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("incorrect@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("wrongpass")

        // Hace click en "Iniciar Sesión"
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        // Espera y verifica que se muestre el mensaje de error (por ejemplo "Credenciales incorrectas")
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Credenciales incorrectas").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Credenciales incorrectas").assertExists()
    }
}
