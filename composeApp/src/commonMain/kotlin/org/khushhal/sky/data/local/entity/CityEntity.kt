package org.khushhal.sky.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity (
    @PrimaryKey val key: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    var temp: Double? = null,
    var description: String? = null,
    var icon: String? = null
)