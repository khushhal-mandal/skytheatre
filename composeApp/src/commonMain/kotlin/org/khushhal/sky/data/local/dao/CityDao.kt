package org.khushhal.sky.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.khushhal.sky.data.local.entity.CityEntity

@Dao
interface CityDao {
    @Upsert
    suspend fun upsertCity(city: CityEntity)

    @Query("SELECT * FROM cities")
    suspend fun getAllCities(): List<CityEntity>

    @Query("DELETE FROM cities WHERE `key` = :key")
    suspend fun deleteCity(key: String)
}