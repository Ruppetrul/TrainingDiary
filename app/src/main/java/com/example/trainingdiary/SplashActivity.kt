package com.example.trainingdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            prepareApp()
        } else {
            setContentView(R.layout.activity_splash)
            prepareApp()
        }
    }

    private fun prepareApp() {
        val database = AppDatabase.getDatabase(applicationContext)

        val seeder = DatabaseSeeder(database)
        seeder.seed().let {
            gotoApp()
        }
    }

    private fun gotoApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}