package com.magistor8.weather.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magistor8.weather.repository.Repository
import com.magistor8.weather.repository.RepositoryImpl
import java.lang.IllegalArgumentException
import java.lang.Thread.sleep
import java.util.*
import kotlin.random.Random

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryImpl: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromLocalSource() = getDataFromLocalSource()

    fun getWeatherFromRemoteSource() = getDataFromLocalSource()

    private fun getDataFromLocalSource() {
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(3000)
            if ((1..2).random() == 1) {
                val weatherData = repositoryImpl.getWeatherFromLocalStorage()
                liveDataToObserve.postValue(AppState.Success(weatherData))
            } else {
                liveDataToObserve.postValue(AppState.Error(IllegalArgumentException()))
            }

        }.start()
    }

}