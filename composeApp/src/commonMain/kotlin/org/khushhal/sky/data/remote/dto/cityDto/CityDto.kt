package org.khushhal.sky.data.remote.dto.cityDto

import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val country: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val name: String? = null,
    val state: String? = null
)