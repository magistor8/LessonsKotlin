package com.magistor8.weather.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magistor8.weather.App.Companion.getHistoryDao
import com.magistor8.weather.repository.LocalRepository
import com.magistor8.weather.repository.LocalRepositoryImpl

class HistoryViewModel (
    val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao())
    ) : ViewModel() {

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        Thread {
            historyLiveData.postValue(AppState.SuccessHistory(historyRepository.getAllHistory()))
        }.start()

    }
}