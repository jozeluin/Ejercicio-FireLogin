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