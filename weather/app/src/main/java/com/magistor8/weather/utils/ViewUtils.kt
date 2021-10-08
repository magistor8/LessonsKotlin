package com.magistor8.weather.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.magistor8.weather.R
import java.util.*

fun View.showSnackBar(
    text: String,
    actionText: String,
    action: (View) -> Unit,
    length: Int = Snackbar.LENGTH_INDEFINITE
) {
    Snackbar.make(this, text, length).setAction(actionText, action).show()
}

private val seasons = arrayOf(
    "winter", "winter", "spring", "spring", "spring", "summer",
    "summer", "summer", "autumn", "autumn", "autumn", "winter"
)

fun getSeason(): String {
    return seasons[Calendar.getInstance().get(Calendar.MONTH)]
}

fun condition(context: Context, condition: String): String {
    with(context) {
        return when(condition) {
            "clear" -> getString(R.string.clear)
            "partly-cloudy" -> getString(R.string.partly_cloudy)
            "cloudy" -> getString(R.string.cloudy)
            "overcast" -> getString(R.string.overcast)
            "drizzle" -> getString(R.string.drizzle)
            "light-rain" -> getString(R.string.light_rain)
            "rain" -> getString(R.string.rain)
            "moderate-rain" -> getString(R.string.moderate_rain)
            "heavy-rain" -> getString(R.string.heavy_rain)
            "continuous-heavy-rain" -> getString(R.string.continuous_heavy_rain)
            "showers" -> getString(R.string.showers)
            "wet-snow" -> getString(R.string.wet_snow)
            "light-snow" -> getString(R.string.light_snow)
            "snow" -> getString(R.string.snow)
            "snow-showers" -> getString(R.string.snow_showers)
            "hail" -> getString(R.string.hail)
            "thunderstorm" -> getString(R.string.thunderstorm)
            "thunderstorm-with-rain" -> getString(R.string.thunderstorm_with_rain)
            "thunderstorm-with-hail" -> getString(R.string.thunderstorm_with_hail)
            else -> getString(R.string.undefined)
        }
    }


}