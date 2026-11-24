# 9-17 Login-Registro con teléfono-SMS #
Vamos a Firebase al y tenemos que habilitar este registro. En Authentication tenemos que darle a Agregar proveedor nuevo, ya teniamos
en el Correo Electronico. Pulsamos a Sign_in method y seleccionamos el telefono. Habilitamos, Le damos a numeros de prueba, ya que si ponemos
el nuestro y trabajamos con el, si le damos un par de veces nos lo bloquea. Le tenemos que poner un numero de teleforno con prefijo, el numero por
ejemplo ser +34 123456 y codigo verificacion 123456, y guardamos.
Depues vamos  a los ajustes de nuestro proyecto por que tenemos que agregar huella digital, el "SHA1" y el "SHA256".

Vamos a nuestro proyecto de android studio, pulsamos en la parte izq, en la pestaña de Gradle-Tasks-android-signinReport, enter.

Hay que tener en cuenta que si subimos la aplicacion a la playStore, vuelve a cambiar el "SHA256", o sea que hay que volverlo a poner porque sino
nos dara error.
Bueno cuando ejecutamos nos salen, los tenemos que copiar y colocar en Firebase, en la huella digital donde corresponda, y guardamos.

Primero vamos a cambiar un poco el diseño de activity_login.xml, porque tenemos que agregar otro boton, vamos a ir agregando botones
uno para cada forma de login. Ahora crearemos el btnLoginPhone, con una icono de telefono.
Entonces cuando lo pulsemos, nos mostrara un dialogo el cual nos dejara poner un telefono y despues no enseñara el codigo de verificacion y seguidamente 
nos logearemos o nos registraremos.
Entonces nos vamos a LoginActivity y dentro del initListeners rellenamos un setOnClickListener del boton que acabamos de crear.
Alli llamamos a una nueva funcion, showPhoneLogin, el cual nos hara enseñar un dialogo. Pero ese dialogo lo tenemos que diseñar.
Creamos un nuevo layout, dialog_phone_login.xml. 
Creamos un textInputLayout, tilPhone, y dentro un TextInputEditText, tiePhone, que tendra de texto nuestro telefono de prueba, +34 123456789.
Depues de eso un boton, btnPhone, con un texto, Send SMS. 

Volvemos a la funcion  showPhoneLogin():
````kotlin
    private fun showPhoneLogin() {
        val phoneBinding : DialogPhoneLoginBinding = DialogPhoneLoginBinding.inflate(layoutInflater)
        val alertdialog= AlertDialog.Builder(this).apply { setView(phoneBinding.root) } .create() // phoneBindig, es el layout que acabamos de diseñar.

        phoneBinding.btnPhone.setOnClickListener {
            loginViewModel.loginWithPhone(phoneBinding.tiePhone.text.toString())

        }
        alertdialog.show()
    }

````
Vamos al viewmodel a crear la nueva funcion loginWithPhone para poder llamar otra funcion nueva que tenemos que crear en authservice, la funcion loginWithPhone()
Aqui en esta funcion del authService tenemos que crear un objeto complejo "PhoneAuthOpions.Builder" el cual sera un argumento para
para poder verificar el numero de telefono atraves de un PhoneAuthProvider.
````kotlin
 fun loginWithPhone(
        phoneNumber: String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        //Para hacer las pruebas con el telefono de prueba que hemos colocado

       // firebaseAuth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+34 123456789","123456")

        val options = PhoneAuthOptions
            .newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)//tiempo hasta enviar otro sms
            .setActivity(activity)//esto es una mala practica pero no ahi opcion, se tendra que ir colocando como argumeto hasta llegar la LoginActivity que se pasara un this
            .setCallbacks(callback)//aqui se tendria que llamar a una serie de funciones implementadas, pero se hara desde el viewModel.
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
````
````kotlin
 override fun onVerificationCompleted(credentials: PhoneAuthCredential)//se manda cuando el numero es de fiar
 override fun onVerificationFailed(p0: FirebaseException) //salta cuando :numero de telefono incorrecto
````
Para saber cuando a mandado el sms para poder sacar el dialogo para poder validarlo.
fun onCodeSent, codigo enviado . He comentado el codigo. En Authservice y en el LoginViewModel.
Despues tenemos que ir al LoginActivity y  en showPhoneLogin(), cuando hacemos la llamada al loginViewModel.loginWithPhone tenemos
que rellenar las lambdas que hemos llamado.

Tenemos que hacer las pruebas con el numero de prueba que pusimos en Firebase. Despues tenemos dos opciones, seguir con un flujo normal ir colcando
los datos mientras nos lo piden o hacer una automatizacion, para pruebas.

AuthService:
````kotlin
   fun loginWithPhone(
        phoneNumber: String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        //Para hacer las pruebas con el telefono de prueba que hemos colocado

        firebaseAuth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+34 123456789","123456")//Automatizacion, le decimos que si
    //llega ese numero de telefono, al sms de repuesta de codigo se ponga ese codigo, y lo hacer todo por detras

        val options = PhoneAuthOptions
            .newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)

````
Con esa automatizacion saltaria directamente al "onVerificationCompleted" del LoginViewModel. Esto lo comentaremos, solo era para pruebas. Ya que si
hacemos esto no se crea ningun login real.

Ahora tendriamos que hacer la vista para colcar los 6 caracteres del pasword  que te piden. Pero vamos a utilizar una libreria de terceros para hacer esto.
utilizaremos esta:
````kotlin
implementation(libs.io.github.chaosleung.pinview)

io-github-chaosleung-pinview = { group = "io.github.chaosleung", name = "pinview", version.ref = "pinview" }

````

Aparte de colcar esto en el lib.versions y en el build.grale.kts(Module:app) tambien tenemos que decirle de donde lo va a descargar, para ello tenemos
que ir al settings.gradle.kts()
 ````kotlin
 dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{url=java.net.URI("https://jitpack.io")}//para descargar librerias de terceros
    }
}
 ````
Ahora nos vamos al dialog_phone_login.xml y colocamos el comoponente nuevo:
````html
    <com.chaos.view.PinView
        android:id="@+id/pinView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="@style/PinWidget.PinView"
        android:visibility="gone"
        android:layout_marginTop="24dp"
        android:cursorVisible="true"
        android:inputType="number"
        android:gravity="center"
        android:itemBackground="@color/white"
        app:hideLineWhenFilled="false"
        app:itemCount="6"
        app:itemRadius="10dp" />

````
LoginActivity, en onCodeSent, le decimos que el pinview se muestre, despues mas abajo que le decimos que haga algo si el texto cambia. 
````kotlin
   private fun showPhoneLogin() {
    val phoneBinding : DialogPhoneLoginBinding = DialogPhoneLoginBinding.inflate(layoutInflater)
    val alertdialog= AlertDialog.Builder(this).apply { setView(phoneBinding.root) } .create()

    phoneBinding.btnPhone.setOnClickListener {
        loginViewModel.loginWithPhone(phoneBinding.tiePhone.text.toString(),this,
            onCodeSent = {
                phoneBinding.tiePhone.isEnabled=false
                phoneBinding.btnPhone.isEnabled=false
                phoneBinding.pinView.isVisible = true
                phoneBinding.pinView.requestFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager//forzar teclado
                imm.showSoftInput(phoneBinding.pinView, InputMethodManager.SHOW_IMPLICIT)

            },
            onVerificationCompleted = {navigateToDetail()},
            onVerificationFailed = {showToast("Ha Habido un Error: $it")}

        )
    }

    phoneBinding.pinView.doOnTextChanged { text, _, _, _ ->//Si el texto cambia,(como dejamos argumentos vacios, colocamos "_")
        if(text?.length == 6){
            loginViewModel.verifyCode(text.toString()){navigateToDetail()}
        }

    }

````

Despues creamos la funcion el viewmodel "verifyCode":
````kotlin
 fun verifyCode(phoneCode: String, onSuccessVerification: () -> Unit) {
        viewModelScope.launch {
            val result=withContext(Dispatchers.IO){
                authService.verifyCode(verificationCode,phoneCode)
            }

            if(result!=null){
                onSuccessVerification()
            }
        }
    }
````
Pero para pasar el verificationCode, este se encuentra en:
````kotlin
override fun onCodeSent(verificationCode: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    //cuando haya enviado el sms al movil
                    this@LoginViewModel.verificationCode = verificationCode
                    _isLoading.value = false
                    onCodeSent()
                }
````
Creamos una variable en el viewmodel de tipo lateinit var, y mediante la linea con el @ se la pasamos de la funcion a la clase.
Habitualmente con un this.verificationCode=verificationCode, hubiese bastado pero como estamos en una corrutina no la encuentra.

Por ultimo en el authService creamos el la funcion verifyCode:
````kotlin
  suspend fun verifyCode(verificationCode: String, phoneCode: String): FirebaseUser? {
        val credentials= PhoneAuthProvider.getCredential(verificationCode,phoneCode)
        return completeRegisterWithPhone(credentials)
    }
````
Y tenemos que crear otra funcion llamada completeRegisterWithPhone, que completa el registro con el numero de telefono.

Entonces esto todo significa que si todo ha ido bien, volviendo al LoginViewModel.verifyCode si el result!=null, entonces onSuccessVerification():
````kotlin
fun verifyCode(phoneCode: String, onSuccessVerification: () -> Unit) {
        viewModelScope.launch {
            val result=withContext(Dispatchers.IO){
                authService.verifyCode(verificationCode,phoneCode)//vendriamos de aqui
            }

            if(result!=null){
                onSuccessVerification()
            }
        }
````

Es la lambda que provocara que naveguemos al detail y nos creara la cuenta.
Si estamos en el caso que ya hemos enviado el codigo del sms, entonces empezariamos desde LoginViewModel.loginWithPhome callBack.onVerificationCompleted()
Dentro de aqui creariamos otra corrutina en la cual comprobamos ese registro atraves del authService.completeRegisterPhone.

Despues ya entramos en el diseño. Aparte vamos a hacer una serie de pequeños cambios para que cuando estemos en la pantalla de colcacion del codigo
no podamos cambiar otra cosa, por eso inhabilitaremos o haremo invisibles ciertos elemntos. Ademas pondremos el foco en el pinview para poder
escribir directamente el codigo y asi ahorrarnos un click. Por eso y para que salga el teclado tenemos que forzarlo:
LoginActivty:
````kotlin
    private fun showPhoneLogin() {
        val phoneBinding : DialogPhoneLoginBinding = DialogPhoneLoginBinding.inflate(layoutInflater)
        val alertdialog= AlertDialog.Builder(this).apply { setView(phoneBinding.root) } .create()

        phoneBinding.btnPhone.setOnClickListener {
            loginViewModel.loginWithPhone(phoneBinding.tiePhone.text.toString(),this,
                onCodeSent = {
                    phoneBinding.tiePhone.isEnabled=false
                    phoneBinding.btnPhone.isEnabled=false
                    phoneBinding.pinView.isVisible = true
                    phoneBinding.pinView.requestFocus()
                    //forzar teclado
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(phoneBinding.pinView, InputMethodManager.SHOW_IMPLICIT)
                    ////

                },
````