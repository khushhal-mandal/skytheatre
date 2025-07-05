package org.khushhal.sky.domain.model

data class Weather (
    val name: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val country: String? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null,
    val description: String? = null,
    val icon: String? = null,
    val main: String? = null,
    val feelsLike: Double? = null,
    val humidity: Int? = null,
    val pressure: Int? = null,
    val seaLevel: Int? = null,
    val temp: Double? = null,
    val tempMax: Double? = null,
    val tempMin: Double? = null,
    val deg: Int? = null,
    val gust: Double? = null
)