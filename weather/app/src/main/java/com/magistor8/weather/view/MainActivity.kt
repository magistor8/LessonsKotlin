package com.magistor8.weather.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.magistor8.weather.R
import com.magistor8.weather.lesson9.ContactsListFragment
import com.magistor8.weather.utils.getSeason
import com.magistor8.weather.view.history.HistoryFragment
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_history ->{
                supportFragmentManager.apply {
                    beginTransaction()
                        .add(R.id.container, HistoryFragment.newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                true
            }
            R.id.menu_content_provider ->{
                supportFragmentManager.apply {
                    beginTransaction()
                        .add(R.id.container, ContactsListFragment.newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }
}