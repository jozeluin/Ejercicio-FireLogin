package com.jozeluindev.firelogin.ui.login

import android.R.id.message
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jozeluindev.firelogin.databinding.ActivityLoginBinding
import com.jozeluindev.firelogin.databinding.DialogPhoneLoginBinding
import com.jozeluindev.firelogin.ui.detail.DetailActivity
import com.jozeluindev.firelogin.ui.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intUi()

    }

    private fun intUi() {
        initListeners()
        initUIState()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.isLoading.collect {
                    binding.loading.isVisible = it
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnLogin.setOnClickListener {
            loginViewModel.login(
                user = binding.tieUser.text.toString(),
                password = binding.tiePassword.text.toString()
            ) {navigateToDetail()}
        }

        binding.tvSignUp.setOnClickListener {
            navigateToSignUp()
        }
        binding.btnLoginPhone.setOnClickListener {
            showPhoneLogin()
        }


    }

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

        phoneBinding.pinView.doOnTextChanged { text, _, _, _ ->
            if(text?.length == 6){
                loginViewModel.verifyCode(text.toString()){navigateToDetail()}
            }

        }

        alertdialog.show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun navigateToSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun navigateToDetail(){
        startActivity(Intent(this, DetailActivity::class.java))


    }

}