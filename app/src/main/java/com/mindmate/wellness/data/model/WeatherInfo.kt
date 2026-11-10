package com.mindmate.wellness.data.model

data class WeatherInfo(
    val cityName: String,
    val temperature: Double,
    val weatherCondition: String,
    val weatherEmoji: String,
    val humidity: Int,
    val wellnessTip: String
)
