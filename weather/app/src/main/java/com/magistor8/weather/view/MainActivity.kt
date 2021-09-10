package com.magistor8.weather.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.magistor8.weather.R
import com.magistor8.weather.view.main.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container,
                MainFragment.newInstance()

            ).addToBackStack("").commit()
        }
    }
}