# 6-17 Registro con Usuario y Contraseña #
Vamos a poner un registro. Vamos al SignUpActivity, al SignUpViewModel y creamos tambien la variable isLoading, y luego en el SignUpActivity
creamos las llamada y los metodos initUI, initUIState y initListeners. Y copiamos el initUIState del anterior. En su layout tambien
colocamos un progressbar. Y dentro del initListeners hacemos que al apretar el boton del su pantalla a traves del binding del boton
activara el signUpViewModel.register que le pasara el contenido del email y del pasword.
Creamos el metodo  register del SignUpViewModel. Inyectamos en el viwmodel el authService.
Y dentro del register creamos la corroutina como en el otro directorio. Y alli dentro ejecutamos el authService.register(email, pasword).
Creamos el metodo de AutService.register(), y alli ejecutamos el firebaseAuth.createUserWithEmailAndPassword(email, password).
y como en el otro caso para cambiar lo que nos devuelve la funcion acabaremos el comando con .await().user, y entonces la funcion register
devolvera un FirebaseUser?. Pero tambien podemos añadir listeners, como herramientas . Pero podemos hacerlo de otra manera:
````kotlin
 suspend fun register(email: String, password: String): FirebaseUser? {
        return suspendCancellableCoroutine { cancellableContinuation->
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val user = it.user
                cancellableContinuation.resume(user)
            }.addOnFailureListener {
                cancellableContinuation.resumeWithException(it)
            }
        }
    }
````
Asi conseguimos que si hay un error que salte una excepcion donde se llama el register. Ques es SignUpViewModel. Asi alli podemos
crear una comprobacion para navegar a navigateToDetail y si no daria un mensaje de error:
````kotlin
class SignUpViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {


    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun register(email: String, password: String, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = withContext(Dispatchers.IO) {
                    authService.register(email, password)
                }
                if (result != null) {
                    navigateToDetail()
                } else {
                    //error
                    Log.i("Josechu Error", "errorrrr")
                }

            } catch (e: Exception) {
                //error
                Log.i("Josechu Error", e.message.orEmpty())
            }
            _isLoading.value = false
        }
    }
}

 
````
Ese navigateToDetail es una funcion lambda, asi donde se llama que es el SignUpActivity.initListiners, tendremos que poner la lambda que llama
a la funcion navigateToDetailt(), que hara lo mismo que en el otro caso, starActivity el DetailActivity. Ahora vamos en el LoginActivity tenemos
que hacer que boton tvSignUp haga algo, que sera llamar al clicar al metodo navigateToSignUp(), que hara que salga la pantalla del SignUpActivity.
Ahora probamos, vamos a registro,pulsando el texto de registro y colocamos el email y el pasword, y asi de esta manera hemos creado otro email con otro password.
Si colocaramos uno ya existente saltaria la excepcion del Catch

# 7-17 Mantener la sesion iniciada #
Ponemos le login y el pasword y entramos. Pero entonces si salimos de la aplicacion y volvemos a entrar ya no estamos dentro, esto lo vamos
a arreglar. No hacemos ningun control sobre si tenemos alguna sesion iniciada. Lo primero que hacemos en el layout es poner un texto
tanto en el email como en la contraseña. Tenemos que hacer un gestor de versiones. Nos vamos al SplashViewModel.  
Creamos una funcion checkDestination(), antes en el SplashActivity al empezar hivamos al navigateToLogin directamente, ahora no lo haremos asi.
En el SplashViewModel, inyectamos el authService  y creamos la funcion isUsserLogged(), que a su vez retornara el valor de otra  funcion nueva
que vamos a crear en el AuthService, fun isUsserLogged(): Boolean. Ahora como Firebase nos dice que estamos logados.
Pues necesitamos el FirebaseUSer, retornamos firebaseAuth.currentUser != null, creamos una funcion auxiliar por si necesitamos esto en mas
parte, getCurrentUser(), osea que lo llamamos y comprobamos que sea diferente de null. Entonces simplemente en SplashViewModel.chekDestination
comprobamos si esta logado. Y ahora comoprobamos y creamos dos opciones:
````kotlin
    private fun isUserLogged():Boolean
    {
        return authService.isUserLogged()
    }

    fun checkDestination(): SplashDestination {
        val isUserLogged = isUserLogged()

       return  if(isUserLogged){
           SplashDestination.Home
        }else{
            SplashDestination.Login
        }

    }
}

sealed class SplashDestination{
    object Login: SplashDestination()
    object Home: SplashDestination()

}
````
Como ves creamos una sealed class con objetos para que sea mas sencillo y fuera de errores de sintaxis, elegir la opcion. Asi donde
lo llamemos podemos utilizar un when y que nos ponga todas la ramas que hay, asi podemos crear mas y nos dara error donde falte:
````kotlin
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (splashViewModel.checkDestination()) {
            SplashDestination.Home -> navigateToHome()
            SplashDestination.Login -> navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
    private fun navigateToHome() {
        startActivity(Intent(this, DetailActivity::class.java))
    }
}


````
AHora si ejecutamos, ahora como estamos logeados ya no vamos a login sino a Home que nos lleva a la pantalla Detail. Ahora aunque salimos en Home que tiene el boton logout,
pero todavia no lo hemos configurado, para logoutarnos tenemos que salir y darle al icono y en la info y borramos el storage.









