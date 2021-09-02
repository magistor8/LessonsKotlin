package com.magistor8.weather.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val liveDataToObserve: MutableLiveData<Any> = MutableLiveData())
    : ViewModel() {

    fun getData(): LiveData<Any> {
        return liveDataToObserve
    }

}