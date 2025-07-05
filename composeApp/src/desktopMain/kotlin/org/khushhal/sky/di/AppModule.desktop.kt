package org.khushhal.sky.di

import org.khushhal.sky.data.local.dao.CityDao
import org.khushhal.sky.data.local.database.AppDatabase
import org.khushhal.sky.data.local.database.DatabaseFactory
import org.khushhal.sky.data.local.database.getDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        val factory = DatabaseFactory()
        val builder = factory.createDatabase()
        getDatabase(builder)
    }
    single<CityDao> { get<AppDatabase>().cityDao() }
}