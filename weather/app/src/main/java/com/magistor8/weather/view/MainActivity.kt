package com.magistor8.weather.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.magistor8.weather.R
import com.magistor8.weather.utils.getSeason
import com.magistor8.weather.view.main.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStartTheme()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container,
                MainFragment.newInstance()

            ).addToBackStack("").commit()
        }
    }

    private fun setStartTheme() {
        when(getSeason()) {
            "winter" -> setTheme(R.style.Theme_Weather_Winter)
            "autumn" -> setTheme(R.style.Theme_Weather_Autumn)
            "summer" -> setTheme(R.style.Theme_Weather_Summer)
            "spring" -> setTheme(R.style.Theme_Weather_Spring)
        }
    }
}