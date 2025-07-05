package org.khushhal.sky.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.khushhal.sky.data.remote.dto.cityDto.CityDto
import org.khushhal.sky.data.remote.dto.weatherdto.WeatherDto

class WeatherApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    encodeDefaults = true
                    isLenient = true
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun getCitySuggestions(query: String): List<CityDto> {
        return httpClient.get {
            url("https://api.openweathermap.org/geo/1.0/direct?")
            parameter("q", query)
            parameter("limit", 5)
            parameter("appid", "add_your_api_key_here")
        }.body()
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String): WeatherDto {
        return httpClient.get {
            url("https://api.openweathermap.org/data/2.5/weather?")
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", units)
            parameter("appid", "add_your_api_key_here")
        }.body()
    }
}