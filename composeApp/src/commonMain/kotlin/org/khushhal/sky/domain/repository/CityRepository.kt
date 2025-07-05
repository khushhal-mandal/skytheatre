package org.khushhal.sky.domain.repository

import org.khushhal.sky.data.local.dao.CityDao
import org.khushhal.sky.data.local.entity.CityEntity

class CityRepository(private val dao: CityDao) {
    suspend fun getAllCities() = dao.getAllCities()
    suspend fun upsertCity(city: CityEntity) = dao.upsertCity(city)
    suspend fun deleteCity(key: String) = dao.deleteCity(key)
}