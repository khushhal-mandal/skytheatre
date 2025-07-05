package org.khushhal.sky.data.remote.dto.weatherdto

import kotlinx.serialization.Serializable

@Serializable
data class Sys(
    val country: String? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null
)