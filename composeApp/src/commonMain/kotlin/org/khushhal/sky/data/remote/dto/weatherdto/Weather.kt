package org.khushhal.sky.data.remote.dto.weatherdto

import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val description: String? = null,
    val icon: String? = null,
    val id: Int? = null,
    val main: String? = null
)