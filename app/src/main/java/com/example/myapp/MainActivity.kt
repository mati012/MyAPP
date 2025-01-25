package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapp.ui.theme.MyappTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

val usuarios = mutableListOf<Pair<String, String>>()
var currentUser: String? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }
        composable("recuperar_contrasena") { RecuperarContrasenaScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.astronauta))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animación Lottie
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo electrónico
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de contraseña con visualización oculta
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) "Ocultar" else "Mostrar"
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(icon)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = androidx.compose.ui.graphics.Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = {
            val userExists = usuarios.any { it.first == email && it.second == password }
            if (userExists) {
                errorMessage = ""
                currentUser = email
                navController.navigate("home")
            } else {
                errorMessage = "Credenciales incorrectas"
            }
        }) { Text("Iniciar Sesión") }

        TextButton(onClick = { navController.navigate("recuperar_contrasena") }) { Text("¿Olvidaste tu contraseña?") }
        TextButton(onClick = { navController.navigate("registro") }) { Text("Registrarse") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Versión 1.0", color = Color.Gray)
    }
}



@Composable
fun RegistroScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Registro",
            fontSize = 30.sp,
            color = androidx.compose.ui.graphics.Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Nombre
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Correo Electrónico
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Contraseña con opción de mostrar/ocultar
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) "Ocultar" else "Mostrar"
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(icon)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Checkbox para aceptar términos y condiciones
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
            Text("Acepto los términos y condiciones")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Registro
        Button(onClick = {
            if (nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank() && termsAccepted) {
                usuarios.add(email to password)
                navController.navigate("login")
            }
        }) {
            Text("Registrar")
        }

        // Botón para volver al Login
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver a Login")
        }
    }
}

@Composable
fun RecuperarContrasenaScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Recuperar Contraseña",
            fontSize = 30.sp,
            color = androidx.compose.ui.graphics.Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Correo Electrónico
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para recuperar contraseña
        Button(onClick = { }) {
            Text("Recuperar Contraseña")
        }

        // Botón para volver al Login
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver a Login")
        }
    }
}
@Composable
fun HomeScreen(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.planeta))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botón Cerrar Sesión en la esquina superior derecha
        Button(
            onClick = {
                navController.navigate("login")
                currentUser = null
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Text("Cerrar Sesión")
        }

        // Texto de bienvenida justo arriba de la animación
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido, ${currentUser ?: "Usuario"}",
                color = androidx.compose.ui.graphics.Color.Black,
                style = androidx.compose.ui.text.TextStyle(fontSize = 30.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animación Lottie
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(300.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeScreen() {
//    HomeScreen(navController = rememberNavController())
//}
