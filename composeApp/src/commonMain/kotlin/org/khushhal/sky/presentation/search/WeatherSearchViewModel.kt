package org.khushhal.sky.presentation.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.khushhal.sky.data.local.entity.CityEntity
import org.khushhal.sky.domain.model.City
import org.khushhal.sky.domain.repository.CityRepository
import org.khushhal.sky.domain.usecase.GetCitySuggestionsUseCase
import org.khushhal.sky.domain.usecase.GetCurrentWeatherUseCase
import org.khushhal.sky.presentation.preference.PreferenceHolder
import org.khushhal.sky.util.ResultState
import org.khushhal.sky.util.toCityEntity
import kotlin.time.Duration.Companion.milliseconds

class WeatherSearchViewModel(
    private val repository: CityRepository,
    private val getCitySuggestionsUseCase: GetCitySuggestionsUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : ViewModel() {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        loadAndRefreshSavedCities()

        coroutineScope.launch {
            searchQuery
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    fun onQueryChanged(query: String) {
        searchQuery.value = query
        _state.value = _state.value.copy(query = query)
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.value = _state.value.copy(suggestions = emptyList(), isLoading = false, error = null)
            return
        }

        coroutineScope.launch {
            getCitySuggestionsUseCase(query).collect { result ->
                when (result) {
                    is ResultState.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                    is ResultState.Success -> _state.value = _state.value.copy(suggestions = result.data, isLoading = false)
                    is ResultState.Failure -> _state.value = _state.value.copy(suggestions = emptyList(), isLoading = false, error = result.message)
                }
            }
        }
    }

    fun loadAndRefreshSavedCities(units: String = PreferenceHolder.unit) {
        coroutineScope.launch {
            val savedCities = repository.getAllCities().toMutableList()

            _state.value = _state.value.copy(savedCities = savedCities, isLoading = false)

            savedCities.forEachIndexed { index, city ->
                launch {
                    when (val result = getCurrentWeatherUseCase(city.lat, city.lon, units)) {
                        is ResultState.Success -> {
                            val weather = result.data
                            val updatedCity = city.copy(
                                temp = weather.temp,
                                description = weather.description,
                                icon = weather.icon
                            )

                            repository.upsertCity(updatedCity)
                            savedCities[index] = updatedCity

                            _state.value = _state.value.copy(savedCities = savedCities.toList())
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    fun saveCity(city: CityEntity) {
        coroutineScope.launch {

            repository.upsertCity(city)


            val weatherResult = getCurrentWeatherUseCase(city.lat, city.lon, PreferenceHolder.unit)

            if (weatherResult is ResultState.Success) {
                val weather = weatherResult.data
                val updatedCity = city.copy(
                    temp = weather.temp,
                    description = weather.description,
                    icon = weather.icon
                )
                repository.upsertCity(updatedCity)
            }


            loadAndRefreshSavedCities()

            _state.value = _state.value.copy(
                query = "",
                suggestions = emptyList()
            )
        }
    }


    fun deleteCity(cityKey: String) {
        coroutineScope.launch {
            repository.deleteCity(cityKey)
            loadAndRefreshSavedCities()
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}


data class SearchState(
    val query: String = "",
    val suggestions: List<City> = emptyList(),
    val savedCities: List<CityEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)