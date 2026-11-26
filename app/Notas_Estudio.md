# 10-17 Login Google #
Habilitamos en Firebase el Google, habra que colcoar la huella digital, ya la pusimos. Colocamos un correo por si los clientes que intenten usar
la app de login, tienen algun problema. Despues tenemos que volver a colocar una copia nueva del google-services.json en nuestro programa.
Tenemos tambien que colocar una nueva dependencia, "implementacion(com.google.android.gms:play-services-auth:20.7.0)". 
Luego vamos a preparar nuestro layout, simplemete colocamos otro boton para google,btnLoginGoogle.
Luego vamos al loginActivity y colocamos el nuevo setOnclickListener del nuevo boton. Este boton activara un launcher en un viewmodel que a su vez
conseguira las credenciales de google de la capa de data.
Llamamos al loginViewModel.onGoogleLoginSelected(), y a su vez llamara a otra funcion ubicada en el AuthService llamada getGoogleClient.
````kotlin
  fun getGoogleClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)).requestEmail()//context inyectado en el constructor
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

````
Para conseguir el contexto se lo inyectamos asi
````kotlin
class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
````
Cuando acabemos de escribir la clase "getGoogleClient", se pondra en rojo el "default_web_client_id", pero al pulsar el martillito 
autamicamente se generara en el string.xml.".
retornaremos un GoogleSignInClient.
Ahora eso lo tenemos que enviar al activity. Para poder enviarlo lo haremo a traves de un lambda. Nos volvemos al ViewModel y creamos una lambda
````kotlin
 fun onGoogleLoginSelected(googleLauncherLogin: (GoogleSignInClient) -> Unit) {
        val gsc=authService.getGoogleClient()
        googleLauncherLogin(gsc)
    } 

````
Y en el activity pasamos la funcion lambda:
````kotlin
 binding.btnLoginGoogle.setOnClickListener {
            loginViewModel.onGoogleLoginSelected {
                googleLauncher.launch(it.signInIntent)//Launcher
            }
        }
````
Aqui dentro creamos el launcher, que es el encargado de lanzar la pantalla, cuando hacemos el login aparece una pantalla para colcar los datos
eso es el launcher. Necesita las credenciales.
Arriba creamos la variable googleLauncher:
````kotlin
 private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {//Si ha ido bien
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)//el result.data son las credenciales
                try {
                    val account =
                        task.getResult(ApiException::class.java)!!//devoulucion parseado a ese modelo de datos
                    loginViewModel.loginWithGoogle(account.idToken!!) { navigateToDetail() }//llamamos al login con las credenciales

                } catch (e: ApiException) {
                    Toast.makeText(this, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }


            }
        }

````
en el loginViewModel tendremos que crear la funcion loginWithGoogle(idToken: String,navigateToDetail: () -> Unit), esta funcion
es el  va hacer el login. Ahora nos tenemos que enganchar otra vez al auth, ya que si no lo hicieramos hariamos login con google pero no
con FireBase, y nos quedariamos en un estado intermedio.
````kotlin
 fun loginWithGoogle(idToken: String,navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                authService.loginWithGoogle(idToken)
            }
````
Ahora creamos la funcion loginWithGoogle en el AuthService, que es quien va a recibir las credenciales:
````kotlin
suspend fun loginWithGoogle(idToken: String): FirebaseUser? {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    return completeRegisterWithCredential(credential)
}
````
Pero como vemos la funcion completeRegisterWithCredential() utilizaba unos datos PhoneAuthCredential y la variable credential utiliza un tipo
AuthCredential y los dos tienen el mismo origen. Podemos utilizar la funcion completeRegisterWithCredential(credential).
Firebase distigue credenciales segun corresponda.
Entonces si volvemos al loginWithGoogle en el viewModel, ya tenemos el resutl que es de tipo FirebaseUser y alli si todo a ido bien ya podemos ir a
detail:
````kotlin
    fun loginWithGoogle(idToken: String,navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                authService.loginWithGoogle(idToken)
            }
            if(result != null){
                navigateToDetail()
            }


        }
    }

````
Nos da un pequeño error relaccionado con el anterior login y lo arreglamos haciendo que en el loginWith phone llame a una nueva funcion
completeRegisterWithPhoneVerification que a su vez llamara a completeRegisterWithCredential.
Ejecutamos y primero sale una pantalla de google eligiendo un usario lo elegimos  y nos lleva a detail y si revisamos el firebase tendremos
otro usuario logeado con google
