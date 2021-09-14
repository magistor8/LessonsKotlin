package com.magistor8.weather.view_model

import com.magistor8.weather.domain_model.Weather

sealed class AppState {
    data class Success(val weatherData: List<Weather>) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
    object Null : AppState()
}
