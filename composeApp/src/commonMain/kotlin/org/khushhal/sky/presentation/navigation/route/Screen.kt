package org.khushhal.sky.presentation.navigation.route

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.khushhal.sky.presentation.preference.AppTheme
import org.khushhal.sky.presentation.preference.PreferenceScreenUI
import org.khushhal.sky.presentation.preference.ThemeController
import org.khushhal.sky.presentation.search.SearchScreenUI
import org.khushhal.sky.presentation.weather.WeatherDetailScreenUI

class SearchScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        SearchScreenUI(
            onCitySelected = { city ->
                navigator?.push(WeatherDetailScreen(lat = city.lat ?: 28.6139, lon = city.lon ?: 77.209))
            },
            onSettingsClick = {
                navigator?.push(PreferenceScreen())
            }
        )
    }
}

data class WeatherDetailScreen(val lat: Double, val lon: Double) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        WeatherDetailScreenUI(lat = lat, lon = lon, onBack = { navigator?.pop() })
    }
}

class PreferenceScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        PreferenceScreenUI(onBack = { navigator?.pop() })
    }
}

