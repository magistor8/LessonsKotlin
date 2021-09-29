package com.magistor8.weather.repository

import com.magistor8.weather.domain_model.Weather

interface LocalRepository {
    fun getAllHistory(): List<Weather>
    fun saveEntity(weather: Weather)
}