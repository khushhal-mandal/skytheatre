package org.khushhal.sky.data.remote.dto.weatherdto

import kotlinx.serialization.Serializable

@Serializable
data class Wind(
    val deg: Int? = null,
    val gust: Double? = null,
    val speed: Double? = null
)