package org.khushhal.sky

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import org.khushhal.sky.data.local.database.DatabaseFactory
import org.khushhal.sky.di.initKoin


fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}