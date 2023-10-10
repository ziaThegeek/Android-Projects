package com.example.kharchabook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 3000 // 3 seconds delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Use a Handler to delay the transition to the main activity
        Handler().postDelayed({
            val mainIntent = Intent(this@MainActivity, MainMenu::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_DELAY)
    }
}