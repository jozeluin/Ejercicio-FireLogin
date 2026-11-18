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

