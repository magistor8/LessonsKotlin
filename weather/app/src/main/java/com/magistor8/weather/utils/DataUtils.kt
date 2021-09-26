package com.magistor8.weather.utils

import com.magistor8.weather.domain_model.FactDTO
import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.domain_model.WeatherDTO
import com.magistor8.weather.domain_model.getDefaultCity

fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!))
}