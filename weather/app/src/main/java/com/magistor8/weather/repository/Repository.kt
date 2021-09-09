package com.magistor8.weather.repository

import com.magistor8.weather.domain_model.Weather

interface Repository {
    fun getWeatherFromServer(): Weather
    fun getWeatherFromLocalStorage(): Weather
}
