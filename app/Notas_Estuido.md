# 5-17 Login con usuario y contraseña #
Creamos la funcion navigateToLogin, dentro de SplashActiviti. :
````kotlin
  private fun navigateToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

````
Ahora sale la pantalla de login, ahora lo que tenemos que hacer es recuperar el login y la contraseña y mandarlo. Vamos a nuestro LoginActivity.
Creamos en nuestro onCreate la llamada a initUi(), y alli llamada a initListeners, y alli con el bindin llamamos a:
````kotlin
    private fun initListeners() {
        binding.btnLogin.setOnClickListener {
            loginViewModel.login(
                user = binding.tieUser.text.toString(),
                password = binding.tiePassword.text.toString()
            ) {navigateToDetail()}
        }
    }
````
Creamos en el viewModel la funcion login. Ese login llamara la capa de data. En ese viewmodel se le tiene que inyectar la capa de data, el servicio
que usemos para crear el login, que sera AuthService, todo el tema de Firebase:
````kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {
    

    fun login(user: String, password: String, navigateToDetail: () -> Unit) {
        .
        .
        .
    }

    class AuthService @Inject constructor(private val firebaseAuth: FirebaseAuth)  {
        suspend fun login(user: String, password: String): FirebaseUser?  {
            return  firebaseAuth.signInWithEmailAndPassword(user, password).await().user
        }

    }
````
Pero como tenemos inyeccion de dependencias, aqui no instanciaremos el firebase si no que lo haremos en otro objeto denominado NetworkModule,
por si un dia lo queremos mockear. Alli dentro vamos a proveer nuestro firebase atraves de un @Module
````kotlin
class AuthService @Inject constructor(private val firebaseAuth: FirebaseAuth) // Ese firebaseAut lo inyectamos aqui...
...
@Module
@InstallIn(SingletonComponent::class)

object NetworkModule {

    @Singleton
    @Provides
    fun provideFireBaseAuth() = FirebaseAuth.getInstance()

}
````
Ahora dentro de AuthService, montamos la funcion login. Las cosas de Firebase son como una caja negra, nosostros le damos los componentes
y el nos lo monta, no sabemos como lo hace, osea que simplemente le proporcionamos el user y el password:
````kotlin
class AuthService @Inject constructor(private val firebaseAuth: FirebaseAuth)  {
    suspend fun login(user: String, password: String): FirebaseUser?  {
       return  firebaseAuth.signInWithEmailAndPassword(user, password).await().user
    }

}
````

Asi ademas de logarlo, tenemos su informacion . Y lo siguiente es inyectarlo en el LoginViewModel. Ahora dentro creamos loa funcion login,
y alli creamos una corutina. Como tenemos que mostrar un progressBar, nos vamos al activity_login y lo creamos.
Entonces en el LoginActivity creamos una funcion intiUistate que tambien se inicia en el initUI, y la creamos. 
Creamos un estado en el loginViewmodel _isLoading  y isLoading. Ahora en el initUiState(), creamos  un lifecycleScope.launch, y alli
collecteamos el isLoading del loginViewModel, y cuando sea true mostrara el progressBar. 
Entonces en el login del viewModel en la corutina es donde se controlara el valor de _isLoading. 
Despues de cuando sea true se consigue el authService.login, y si ese result es diferente de null ejecutara una funcion lambda de navegacion
.Esa lambda sera ejecutada en donde se llama el loginViewModel.login, que es en el loginActivity, alli ejecutara la funcion
navigateToDetail(), la cual ejecutara el starActivity del DetailActivity.

Lo probamos y da error. Y es un tema de versiones de kotlin, y por que le faltaba el jvmToolChain().
Ahora probamos y colcamos lo que tenemos puesto en la base de datos, el usuario y la contraseña. Y Funciona correctamente, tenemos el login implementado,
.


 