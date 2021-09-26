package com.magistor8.weather.repository

import com.magistor8.weather.domain_model.WeatherDTO
import retrofit2.Call
import retrofit2.http.*

interface WeatherAPI {
    @GET("v2/informers")
    fun getWeather(
        @Header("X-Yandex-API-Key") token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherDTO>

}