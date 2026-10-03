package com.jozeluindev.firelogin.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jozeluindev.firelogin.databinding.ActivityLoginBinding
import com.jozeluindev.firelogin.ui.detail.DetailActivity
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
    }

    private fun navigateToDetail(){
        startActivity(Intent(this, DetailActivity::class.java))


    }

}