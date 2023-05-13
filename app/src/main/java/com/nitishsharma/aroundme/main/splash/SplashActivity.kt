package com.nitishsharma.aroundme.main.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.nitishsharma.aroundme.R
import com.nitishsharma.aroundme.main.maps.MapsActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        moveToMainMap()
    }

    private fun moveToMainMap() {
        Handler().postDelayed({
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }, 2500)
    }
}