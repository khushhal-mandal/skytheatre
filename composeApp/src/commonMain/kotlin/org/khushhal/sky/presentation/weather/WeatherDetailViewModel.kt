package org.khushhal.sky.presentation.weather

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.khushhal.sky.data.repository.WeatherRepositoryImpl
import org.khushhal.sky.domain.model.Weather
import org.khushhal.sky.domain.repository.CityRepository
import org.khushhal.sky.domain.usecase.GetCurrentWeatherUseCase
import org.khushhal.sky.util.ResultState

class WeatherDetailViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
): ViewModel()
{
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow(WeatherDetailState())
    val state: StateFlow<WeatherDetailState> = _state.asStateFlow()

    fun fetchWeather(lat: Double, lon: Double, units: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)

        coroutineScope.launch {
            when (val result = getCurrentWeatherUseCase(lat, lon, units)) {
                is ResultState.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        weather = result.data,
                        error = null
                    )
                }

                is ResultState.Failure -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                        weather = null
                    )
                }

                else -> {}
            }
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }
}

data class WeatherDetailState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val error: String? = null
)