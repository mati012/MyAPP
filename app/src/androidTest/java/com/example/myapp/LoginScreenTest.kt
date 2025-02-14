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

        composeTestRule.onNodeWithText("Correo electrónico").assertExists()

        composeTestRule.onNodeWithText("Contraseña").assertExists()

        composeTestRule.onNodeWithText("Iniciar Sesión").assertExists()

        composeTestRule.onNodeWithText("Registrarse").assertExists()
        composeTestRule.onNodeWithText("¿Olvidaste tu contraseña?").assertExists()
    }

    @Test
    fun loginScreen_textInput() {
        val testEmail = "test@example.com"
        val testPassword = "123456"


        composeTestRule.onNodeWithText("Correo electrónico").performTextInput(testEmail)

        composeTestRule.onNodeWithText("Contraseña").performTextInput(testPassword)


        composeTestRule.onNodeWithText(testEmail).assertExists()
        composeTestRule.onNodeWithText(testPassword).assertExists()
    }

    @Test
    fun loginScreen_invalidCredentialsShowsError() {

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("incorrect@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("wrongpass")


        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()


        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Credenciales incorrectas").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Credenciales incorrectas").assertExists()
    }
}
