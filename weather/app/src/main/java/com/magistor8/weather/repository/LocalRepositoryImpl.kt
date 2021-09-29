package com.magistor8.weather.repository

import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.room.HistoryDao
import com.magistor8.weather.utils.convertHistoryEntityToWeather
import com.magistor8.weather.utils.convertWeatherToEntity

class LocalRepositoryImpl(private val localDataSource: HistoryDao):LocalRepository {

    override fun getAllHistory(): List<Weather> {
        return convertHistoryEntityToWeather(localDataSource.all())
    }

    override fun saveEntity(weather: Weather) {
        Thread {
            localDataSource.insert(convertWeatherToEntity(weather))
        }.start()
    }

}