package org.khushhal.sky

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.khushhal.sky.presentation.navigation.route.SearchScreen
import org.khushhal.sky.presentation.preference.AppTheme
import org.khushhal.sky.presentation.preference.ThemeController


@Composable
fun App() {
    val theme by ThemeController.theme.collectAsState()


    val isDarkTheme = when (theme) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Navigator(SearchScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
