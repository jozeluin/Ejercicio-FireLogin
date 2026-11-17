# 3-17 Creando la logica de al app (Login,Registro y detalle) #
- Colocamos las dependencias necesarias.
- Creamos FireLoginApp, para inicializar Hilt
- Colocamos FireLoginApp en el manifest en el atributo name
- Creamos directorios ui,data
- En ui creamos los directorios detail,login,splash
- Arrastramos el  mainActivity y le cambiamos el nombre por SplashActivity
- Tambien cambiamos el nombre el layout por activity_splash.xml
- Creamos el SplashViewModel en ui.splash

En el SplashActivity creamos el binding y el viewModel. Lo activamos en el build.gradle.kts. Y creamos el binding
en el SplasActivity y lo colocamos en el onCreate.
Y ahora dentro del signup creamos los archivos SignUpActivity, SignUpViewModel. Y hacemos lo mismo que hemos hecho
con el splash.
Y lo mismo con el login y con detail.

# 4-17 Creando el diseño de al app (Login,Registro y detalle) #
Creamos en el activity_splash.xml colocamo un background para ver que se lanza. Creamos un color en colors.xml, el color splash . Tambien colocamos un texto.

Vamos al activity_login.cml, tendremos que crear una imagen, tambien tendremos que crear unos botones pero eso lo haremos mas tarde.
Colocamos tambien un TextInputLayout y un TextInputLayout.
Dentro del TextInputLayout colocamos un TextInputEditText, basicamente Usuario y pasword, y abajo el boton de login.

Ahora despues activity_detail.xml. Simplemente colocaremos un boton, Logout. Para saber si tenemos la sesion iniciada.
Ahora vamos al registro, activity_sign_up.xml. Copiamos el activity_login.xml, sin la imagen, tendremos solo dos campos y un boton, SignUp.
Uno de email y otro de password.
