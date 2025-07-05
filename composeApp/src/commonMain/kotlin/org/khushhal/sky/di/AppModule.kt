package org.khushhal.sky.di

import org.khushhal.sky.data.remote.WeatherApi
import org.khushhal.sky.data.repository.WeatherRepositoryImpl
import org.khushhal.sky.domain.repository.CityRepository
import org.khushhal.sky.domain.repository.WeatherRepository
import org.khushhal.sky.domain.usecase.GetCitySuggestionsUseCase
import org.khushhal.sky.domain.usecase.GetCurrentWeatherUseCase
import org.khushhal.sky.presentation.search.WeatherSearchViewModel
import org.khushhal.sky.presentation.weather.WeatherDetailViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { CityRepository(get()) }
    singleOf(::WeatherApi)
    singleOf(::WeatherRepositoryImpl).bind(WeatherRepository::class)
    factory { GetCitySuggestionsUseCase(get()) }
    factory { GetCurrentWeatherUseCase(get()) }
    factory { WeatherSearchViewModel(get(), get(), get()) }
    factory { WeatherDetailViewModel(get()) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, platformModule)
    }
}