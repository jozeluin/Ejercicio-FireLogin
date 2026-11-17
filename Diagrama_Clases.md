# Diagrama de Clases - FireLogin

Este documento contiene la arquitectura actual del proyecto **FireLogin** basada en **MVVM (Model-View-ViewModel)** e **Inyección de Dependencias con Hilt**.

```mermaid
classDiagram
    direction TB

    %% Application & DI Layer
    class FireLoginApp {
        +onCreate()
    }

    class NetworkModule {
        +provideFireBaseAuth() FirebaseAuth
    }

    %% Data Layer
    class AuthService {
        -firebaseAuth: FirebaseAuth
        +login(user: String, password: String) FirebaseUser?
    }

    %% UI & ViewModel Layer - Splash
    class SplashActivity {
        -splashViewModel: SplashViewModel
        -binding: ActivitySplashBinding
        +onCreate(savedInstanceState: Bundle)
        -navigateToLogin()
    }

    class SplashViewModel {
    }

    %% UI & ViewModel Layer - Login
    class LoginActivity {
        -loginViewModel: LoginViewModel
        -binding: ActivityLoginBinding
        +onCreate(savedInstanceState: Bundle)
        -intUi()
        -initUIState()
        -initListeners()
        -navigateToDetail()
    }

    class LoginViewModel {
        -authService: AuthService
        -_isLoading: MutableStateFlow~Boolean~
        +isLoading: StateFlow~Boolean~
        +login(user: String, password: String, navigateToDetail: Function)
    }

    %% UI & ViewModel Layer - SignUp
    class SignUpActivity {
        -signUpViewModel: SignUpViewModel
        -binding: ActivitySignUpBinding
        +onCreate(savedInstanceState: Bundle)
    }

    class SignUpViewModel {
    }

    %% UI & ViewModel Layer - Detail
    class DetailActivity {
        -detailViewModel: DetailViewModel
        -binding: ActivityDetailBinding
        +onCreate(savedInstanceState: Bundle)
    }

    class DetailViewModel {
    }

    %% Relationships & Dependencies
    FireLoginApp ..> NetworkModule : Hilt Initialization
    NetworkModule ..> AuthService : Injects FirebaseAuth
    AuthService <-- LoginViewModel : Injects AuthService

    SplashActivity --> SplashViewModel : observe / use
    SplashActivity ..> LoginActivity : navigates to

    LoginActivity --> LoginViewModel : observe / use
    LoginActivity ..> DetailActivity : navigates to

    SignUpActivity --> SignUpViewModel : observe / use

    DetailActivity --> DetailViewModel : observe / use
```

---

## Resumen de la Arquitectura

### 1. **Capa de Aplicación y DI (Inyección de Dependencias)**
* **`FireLoginApp`**: Clase principal que hereda de `Application` con la anotación `@HiltAndroidApp` para iniciar Hilt.
* **`NetworkModule`**: Módulo Hilt que provee la instancia Singleton de `FirebaseAuth`.

### 2. **Capa de Datos (`data`)**
* **`AuthService`**: Encargado de la comunicación directa con Firebase Auth. Recibe `FirebaseAuth` por inyección y expone métodos de autenticación (como `login()`).

### 3. **Capa de Presentación (`ui`) - MVVM**
* **Splash (`SplashActivity` / `SplashViewModel`)**: Pantalla inicial de carga que redirige a `LoginActivity`.
* **Login (`LoginActivity` / `LoginViewModel`)**: Maneja la vista de inicio de sesión con correo y contraseña. Observa el estado `isLoading` del ViewModel y comunica con `AuthService`.
* **Registro (`SignUpActivity` / `SignUpViewModel`)**: Pantalla para registrar nuevos usuarios.
* **Detalle (`DetailActivity` / `DetailViewModel`)**: Pantalla principal tras iniciar sesión exitosamente.
