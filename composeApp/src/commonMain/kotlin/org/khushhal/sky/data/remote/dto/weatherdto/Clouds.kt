package org.khushhal.sky.data.remote.dto.weatherdto

import kotlinx.serialization.Serializable

@Serializable
data class Clouds(
    val all: Int? = null
)