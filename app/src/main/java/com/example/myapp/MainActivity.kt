package com.example.myapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.example.myapp.ui.theme.MyappTheme
import com.google.firebase.database.*
import com.google.android.gms.location.LocationServices
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource


// Variable global para almacenar el usuario actual (se recomienda usar ViewModel para producción)
var currentUser: String? = null

// Data class para representar al usuario
data class User(
    val id: String? = null,
    val nombre: String = "",
    val email: String = "",
    val password: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyappTheme {
                AppNavigation()
            }
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
        composable("escribir") { EscribirScreen(navController) }
        composable("hablar") { HablarScreen(navController) }
        composable("buscarDispositivo") { BuscarDispositivoScreen(navController) }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    // Referencia a la base de datos de Firebase
    val database = FirebaseDatabase.getInstance().reference

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Animación Lottie (asegúrate de tener el recurso R.raw.astronauta)
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
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) "Ocultar" else "Mostrar"
                TextButton(onClick = { passwordVisible = !passwordVisible }) { Text(icon) }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(onClick = {
            // Buscar el usuario por email en Realtime Database
            database.child("users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var foundUser: User? = null
                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(User::class.java)
                                if (user != null && user.email == email && user.password == password) {
                                    foundUser = user
                                    break
                                }
                            }
                            if (foundUser != null) {
                                errorMessage = ""
                                currentUser = foundUser.email
                                navController.navigate("home")
                            } else {
                                errorMessage = "Credenciales incorrectas"
                            }
                        } else {
                            errorMessage = "Credenciales incorrectas"
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        errorMessage = error.message
                    }
                })
        }) {
            Text("Iniciar Sesión")
        }
        TextButton(onClick = { navController.navigate("recuperar_contrasena") }) {
            Text("¿Olvidaste tu contraseña?")
        }
        TextButton(onClick = { navController.navigate("registro") }) {
            Text("Registrarse")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Versión 1.0", color = Color.Gray)
    }
}

@Composable
fun RegistroScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro", fontSize = 30.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) "Ocultar" else "Mostrar"
                TextButton(onClick = { passwordVisible = !passwordVisible }) { Text(icon) }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
            Text("Acepto los términos y condiciones")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank() && termsAccepted) {
                database.child("users").orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                errorMessage = "El correo ya está registrado."
                            } else {
                                val newUserRef = database.child("users").push()
                                val userId = newUserRef.key
                                val newUser = User(id = userId, nombre = nombre, email = email, password = password)
                                newUserRef.setValue(newUser).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate("login")
                                    } else {
                                        errorMessage = task.exception?.message ?: "Error al registrar"
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            errorMessage = error.message
                        }
                    })
            }
        }) {
            Text("Registrar")
        }
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color.Red)
        }
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver a Login")
        }
    }
}

@Composable
fun RecuperarContrasenaScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference

    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Recuperar Contraseña", fontSize = 30.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nueva Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            database.child("users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var updated = false
                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(User::class.java)
                                if (user != null && user.email == email) {
                                    userSnapshot.ref.child("password").setValue(newPassword)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                successMessage = "Contraseña actualizada"
                                                errorMessage = ""
                                            } else {
                                                errorMessage = "Error al actualizar la contraseña"
                                                successMessage = ""
                                            }
                                        }
                                    updated = true
                                    break
                                }
                            }
                            if (!updated) {
                                errorMessage = "No se pudo actualizar la contraseña"
                            }
                        } else {
                            errorMessage = "Correo no registrado"
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        errorMessage = error.message
                    }
                })
        }) {
            Text("Recuperar Contraseña")
        }
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color.Red)
        }
        if (successMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = successMessage, color = Color.Green)
        }
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver a Login")
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    // Animación del planeta
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.planeta))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Cabecera con título y botón de cerrar sesión
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bienvenido,\n${currentUser?.substringBefore('@') ?: "Usuario"}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = {
                    currentUser = null
                    navController.navigate("login")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar Sesión",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }

        // Contenido principal centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación del planeta
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 32.dp)
            )

            // Tarjetas de funcionalidades
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Accesibilidad",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Botón Escribir
                        Button(
                            onClick = { navController.navigate("escribir") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Escribir",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Escribir")
                            }
                        }

                        // Botón Hablar
                        Button(
                            onClick = { navController.navigate("hablar") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Hablar",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Hablar")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón Buscar Dispositivo
                    Button(
                        onClick = { navController.navigate("buscarDispositivo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Buscar Dispositivo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscar Dispositivo")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EscribirScreen(navController: NavController) {
    val context = LocalContext.current
    var textToSpeak by remember { mutableStateOf("") }

    // Usamos un estado para almacenar la instancia de TextToSpeech
    val ttsState = remember { mutableStateOf<TextToSpeech?>(null) }

    // Inicializamos el TextToSpeech y configuramos el idioma en el callback de onInit
    LaunchedEffect(Unit) {
        ttsState.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsState.value?.language = Locale.getDefault()
            }
        }
    }

    // Aseguramos liberar el recurso cuando se desmonte el composable
    DisposableEffect(ttsState.value) {
        onDispose {
            ttsState.value?.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Escribir: Ingrese el texto y escúchelo", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = textToSpeak,
            onValueChange = { textToSpeak = it },
            label = { Text("Texto a reproducir") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            ttsState.value?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "tts1")
        }) {
            Text("Reproducir")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("home") }) {
            Text("Volver al Menú")
        }
    }
}


@Composable
fun HablarScreen(navController: NavController) {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("") }
    // Launcher para el intent de reconocimiento de voz
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                recognizedText = matches[0]
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hablar: Presione para hablar", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Crea el intent para reconocimiento de voz
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
            }
            speechLauncher.launch(intent)
        }) {
            Text("Iniciar Reconocimiento de Voz")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Texto reconocido: $recognizedText")
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("home") }) {
            Text("Volver al Menú")
        }
    }
}

@Composable
fun BuscarDispositivoScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationText by remember { mutableStateOf("Ubicación no disponible") }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    locationText = "Lat: ${location.latitude}, Lon: ${location.longitude}"
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Buscar Dispositivo (Geolocalización)", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(locationText)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("home") }) {
            Text("Volver al Menú")
        }
    }
}