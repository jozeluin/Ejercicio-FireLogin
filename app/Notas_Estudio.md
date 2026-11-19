# 8-17 Logout #
Vamos al DetailActivity, y creamos lo de siempre:
-  Dentro del onCreate creamos el binding, y lo colocamos en el setContentView(binding.root),
- Despues llamamos al initUI()
- De este a initListeners()
- Alli gentionamos el listener del btnLogout que llamara a la funcion logout del viewModel

Inyectamos en el viewModel el authService, cuando hacemos logout firebase no nos da nada.
HAcemos la corutina:

````kotlin
fun logout(navigateToLogin: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authService.logout()

        }
        navigateToLogin()

    }

//En AuthService.kt

fun logout() {
    firebaseAuth.signOut()
}


````
La lambda se ejecutara en el DetailActivity, y se crea alli la funcion:
````kotlin
  private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        )

    }
````
Para poder borrar la pila tenemos que hacer esto de los flags con el .apply.

