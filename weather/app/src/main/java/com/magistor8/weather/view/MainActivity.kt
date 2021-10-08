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
import com.magistor8.weather.view.map.MapsFragment

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
                val historyFragment = supportFragmentManager.findFragmentByTag(getString(R.string.historyFragment))
                if (historyFragment==null) {
                    supportFragmentManager.apply {
                        beginTransaction()
                            .add(R.id.container, HistoryFragment.newInstance(), getString(R.string.historyFragment))
                            .addToBackStack("")
                            .commitAllowingStateLoss()
                    }
                }
                true
            }
            R.id.menu_content_provider ->{
                val contactsListFragment = supportFragmentManager.findFragmentByTag(getString(R.string.contactsListFragment))
                if (contactsListFragment==null) {
                    supportFragmentManager.apply {
                        beginTransaction()
                            .add(R.id.container, ContactsListFragment.newInstance(), getString(R.string.contactsListFragment))
                            .addToBackStack("")
                            .commitAllowingStateLoss()
                    }
                }
                true
            }

            R.id.menu_google_maps -> {
                val mapsFragment = supportFragmentManager.findFragmentByTag(getString(R.string.MapsFragment))
                if (mapsFragment==null) {
                    supportFragmentManager.apply {
                        beginTransaction()
                            .add(R.id.container, MapsFragment(), getString(R.string.MapsFragment))
                            .addToBackStack("")
                            .commitAllowingStateLoss()
                    }
                }
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }
}