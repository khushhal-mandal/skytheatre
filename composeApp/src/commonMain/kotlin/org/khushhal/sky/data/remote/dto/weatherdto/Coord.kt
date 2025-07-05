package org.khushhal.sky.data.remote.dto.weatherdto

import kotlinx.serialization.Serializable

@Serializable
data class Coord(
    val lat: Double? = null,
    val lon: Double? = null
)