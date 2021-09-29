package com.magistor8.weather.utils

import com.magistor8.weather.domain_model.*
import com.magistor8.weather.room.HistoryEntity

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    val fact: FactDTO = weatherDTO.fact!!
    return Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!,
        fact.icon!!, fact.wind_speed!!, fact.humidity!!, fact.season!!)
}

fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>): List<Weather> {
    return entityList.map {
        Weather(
            City(it.city, 0.0, 0.0),
            it.temperature,
            0,
            it.condition,
            "skc_n",
            it.wind,
            it.humidity,
            "autumn"
        )
    }
}

fun convertWeatherToEntity(weather: Weather): HistoryEntity {
    return HistoryEntity(
        0,
        weather.city.name,
        weather.temperature,
        weather.condition,
        weather.wind,
        weather.humidity
    )
}
