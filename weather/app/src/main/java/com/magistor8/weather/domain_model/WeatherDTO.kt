package com.magistor8.weather.domain_model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherDTO(
    val fact: FactDTO?
):Parcelable

@Parcelize
data class FactDTO(
    val temp: Int?,
    val feels_like: Int?,
    val condition: String?
):Parcelable