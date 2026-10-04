package com.jozeluindev.firelogin.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jozeluindev.firelogin.databinding.ActivitySplashBinding
import com.jozeluindev.firelogin.ui.detail.DetailActivity
import com.jozeluindev.firelogin.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val splashViewModel :SplashViewModel by viewModels()

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

       when(splashViewModel.checkDestination()) {
           SplashDestination.Home -> navigateToHome()
           SplashDestination.Login -> navigateToLogin()
       }


    }

   private fun navigateToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
    }
    private fun navigateToHome(){
        startActivity(Intent(this, DetailActivity::class.java))
    }

}