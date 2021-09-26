package com.magistor8.weather.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
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

fun getSeason(): String? {
    return seasons[Calendar.getInstance().get(Calendar.MONTH)]
}