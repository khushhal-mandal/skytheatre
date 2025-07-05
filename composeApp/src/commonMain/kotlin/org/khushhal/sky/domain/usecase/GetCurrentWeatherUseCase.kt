package org.khushhal.sky.domain.usecase

import org.khushhal.sky.data.repository.WeatherRepositoryImpl
import org.khushhal.sky.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, units: String) = repository.getCurrentWeather(lat, lon, units)
}