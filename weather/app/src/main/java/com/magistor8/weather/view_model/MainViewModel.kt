package com.magistor8.weather.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.repository.Repository
import com.magistor8.weather.repository.RepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryImpl: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(isRussian = true)

    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(isRussian = false)

    private fun getDataFromLocalSource(isRussian: Boolean) {
        var weatherData: List<Weather>
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(3000)
            weatherData = if (isRussian) {
                repositoryImpl.getWeatherFromLocalStorageRus()
            } else {
                repositoryImpl.getWeatherFromLocalStorageWorld()
            }
            liveDataToObserve.postValue(AppState.Success(weatherData))
        }.start()
    }

}