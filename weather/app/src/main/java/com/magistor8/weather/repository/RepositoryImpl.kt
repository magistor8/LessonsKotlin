package com.magistor8.weather.repository

import com.magistor8.weather.domain_model.Weather

class RepositoryImpl : Repository {

    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherFromLocalStorage(): Weather {
        return Weather()
    }
}
