package com.magistor8.weather.repository

import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.domain_model.getRussianCities
import com.magistor8.weather.domain_model.getWorldCities

class RepositoryImpl : Repository {

    override fun getWeatherFromServer(): Weather = Weather()

    override fun getWeatherFromLocalStorageRus(): List<Weather> = getRussianCities()

    override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()
}
