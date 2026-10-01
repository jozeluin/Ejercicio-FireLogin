package com.jozeluindev.firelogin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}

//Acuerdate de cambiar el  namespace y el applicationId en el build.gradle.kts (app)