package org.khushhal.sky.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.khushhal.sky.data.repository.WeatherRepositoryImpl
import org.khushhal.sky.domain.model.City
import org.khushhal.sky.domain.repository.WeatherRepository
import org.khushhal.sky.util.ResultState

class GetCitySuggestionsUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke(query: String): Flow<ResultState<List<City>>> {
        return repository.getCitySuggestions(query)
    }
}