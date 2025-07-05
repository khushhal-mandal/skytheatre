package org.khushhal.sky.util

import org.khushhal.sky.data.local.entity.CityEntity
import org.khushhal.sky.data.remote.dto.cityDto.CityDto
import org.khushhal.sky.data.remote.dto.weatherdto.WeatherDto
import org.khushhal.sky.domain.model.City
import org.khushhal.sky.domain.model.Weather

fun CityDto.toCity(): City = City(
    country = country,
    lat = lat,
    lon = lon,
    name = name,
    state = state
)

fun WeatherDto.toWeather(): Weather = Weather(
    name = name,
    lat = coord?.lat,
    lon = coord?.lon,
    country = sys?.country,
    sunrise = sys?.sunrise,
    sunset = sys?.sunset,
    description = weather?.getOrNull(0)?.description,
    icon = weather?.getOrNull(0)?.icon,
    main = weather?.getOrNull(0)?.main,
    feelsLike = main?.feels_like,
    humidity = main?.humidity,
    pressure = main?.pressure,
    seaLevel = main?.sea_level,
    temp = main?.temp,
    tempMax = main?.temp_max,
    tempMin = main?.temp_min,
    deg = wind?.deg,
    gust = wind?.gust
)

fun Weather.toCityEntity(existing: CityEntity): CityEntity {
    return existing.copy(
        temp = this.temp,
        description = this.description,
        icon = this.icon
    )
}

fun City.toCityEntity(): CityEntity {
    val generatedKey = "${lat ?: 0.0}_${lon ?: 0.0}"
    return CityEntity(
        key = generatedKey,
        name = name ?: "Unknown",
        lat = lat ?: 0.0,
        lon = lon ?: 0.0,
        temp = null,
        description = null,
        icon = null
    )
}

fun CityEntity.toCity(): City {
    return City(
        name = name,
        lat = lat,
        lon = lon,
        country = null,
        state = null
    )
}