package org.khushhal.sky.presentation.preference

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferenceViewModel {

    private val _state = MutableStateFlow(
        PreferenceState(
            selectedUnit = PreferenceHolder.unit,
            selectedTheme = PreferenceHolder.theme
        )
    )
    val state: StateFlow<PreferenceState> = _state

    fun updateUnit(unit: String) {
        PreferenceHolder.unit = unit
        _state.value = _state.value.copy(selectedUnit = unit)
    }

    fun updateTheme(theme: String) {
        PreferenceHolder.theme = theme
        _state.value = _state.value.copy(selectedTheme = theme)
    }
}

object ThemeController {
    private val _theme = MutableStateFlow(AppTheme.valueOf(PreferenceHolder.theme))
    val theme: StateFlow<AppTheme> = _theme

    fun updateTheme(newTheme: AppTheme) {
        PreferenceHolder.theme = newTheme.name
        _theme.value = newTheme
    }
}

enum class AppTheme {
    System, Light, Dark
}

data class PreferenceState(
    val selectedUnit: String = "metric",
    val selectedTheme: String = "System"
)

object PreferenceHolder {
    var unit: String = "metric"
    var theme: String = AppTheme.System.name
}
