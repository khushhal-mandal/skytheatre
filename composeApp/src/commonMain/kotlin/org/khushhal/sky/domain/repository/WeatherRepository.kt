package org.khushhal.sky.domain.repository

import kotlinx.coroutines.flow.Flow
import org.khushhal.sky.domain.model.City
import org.khushhal.sky.domain.model.Weather
import org.khushhal.sky.util.ResultState

interface WeatherRepository {
    fun getCitySuggestions(query: String): Flow<ResultState<List<City>>>
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String): ResultState<Weather>
}