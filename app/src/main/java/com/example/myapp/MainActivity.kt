package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login"){
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
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") })
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
    }
}

@Composable
fun RegistroScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") })
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
            Text("Acepto los términos y condiciones")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank() && termsAccepted) {
                usuarios.add(email to password)
                navController.navigate("login")
            }
        }) { Text("Registrar") }
        TextButton(onClick = { navController.navigate("login") }) { Text("Volver a Login") }
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
        TextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Lógica para enviar correo */ }) { Text("Recuperar Contraseña") }
        TextButton(onClick = { navController.navigate("login") }) { Text("Volver a Login") }
    }
}
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido, ${currentUser ?: "Usuario"}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("login")
            currentUser = null
        }) { Text("Cerrar Sesión") }
    }
}
