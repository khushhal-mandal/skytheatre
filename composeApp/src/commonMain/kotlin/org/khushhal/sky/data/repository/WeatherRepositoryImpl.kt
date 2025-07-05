package org.khushhal.sky.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.khushhal.sky.data.remote.WeatherApi
import org.khushhal.sky.domain.model.City
import org.khushhal.sky.domain.model.Weather
import org.khushhal.sky.domain.repository.WeatherRepository
import org.khushhal.sky.util.ResultState
import org.khushhal.sky.util.toCity
import org.khushhal.sky.util.toWeather

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {
    override fun getCitySuggestions(query: String): Flow<ResultState<List<City>>> = flow {
        emit(ResultState.Loading)
        try {
            val result = api.getCitySuggestions(query).map { it.toCity() }
            emit(ResultState.Success(result))
        } catch (e: Exception) {
            emit(ResultState.Failure(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String
    ): ResultState<Weather> {
        return try {
            val result = api.getCurrentWeather(lat, lon, units)
            ResultState.Success(result.toWeather())
        } catch (e: Exception) {
            ResultState.Failure(e.message ?: "Unknown error")
        }
    }
}