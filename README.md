# MyAPP
 
MyAPP es una aplicación Android desarrollada en Android Studio con Kotlin y Jetpack Compose. La aplicación está diseñada como una herramienta de accesibilidad para personas con discapacidad sensorial del habla, facilitando la comunicación a través de múltiples funcionalidades como conversión de texto a voz, reconocimiento de voz y geolocalización. Además, integra un back-end basado en Firebase Realtime Database para la gestión y persistencia de datos de usuarios.

Características
Autenticación de Usuarios:

Registro: Los usuarios pueden registrarse proporcionando nombre, correo electrónico, contraseña y aceptando los términos y condiciones.
Inicio de Sesión: Permite el acceso a usuarios registrados mediante validación con Firebase Realtime Database.
Recuperación de Contraseña: Funcionalidad para actualizar la contraseña en caso de olvido.
Funciones de Accesibilidad:

Escribir (Text-to-Speech): Permite escribir texto y reproducirlo mediante conversión de texto a voz.
Hablar (Speech Recognition): Captura la voz del usuario y la convierte a texto.
Buscar Dispositivo (Geolocalización): Obtiene la ubicación actual del dispositivo.
Interfaz de Usuario Moderna:

Creada con Jetpack Compose para una experiencia fluida y responsiva.
Integración de animaciones con Lottie para mejorar la interacción.
Integración con Firebase:

Utiliza Firebase Realtime Database para el almacenamiento y manejo de datos de usuario.
Uso de Firebase Analytics para rastrear eventos dentro de la aplicación.
Tecnologías Utilizadas
Lenguaje de Programación: Kotlin
UI Framework: Jetpack Compose
Back-End: Firebase Realtime Database, Firebase Analytics
Animaciones: Lottie Compose
Geolocalización: Google Play Services Location
Testing: JUnit, Mockito, Robolectric, Espresso, Compose Testing, Firebase Test Lab
Control de Versiones: Git (GitHub)
Sistema de Construcción: Gradle (Kotlin DSL)

Arquitectura
La aplicación sigue un enfoque modular y de separación de responsabilidades:

Presentación (UI): Implementada en Jetpack Compose para crear interfaces modernas y responsivas.
Lógica de Negocio: Implementada a través de funciones y (opcionalmente) ViewModels que gestionan la validación y manejo de datos.
Persistencia de Datos: Firebase Realtime Database se utiliza para almacenar y recuperar la información de los usuarios.
Navegación: Se usa Compose Navigation para gestionar el flujo entre pantallas.
