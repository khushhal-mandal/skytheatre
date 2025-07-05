package org.khushhal.sky

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.khushhal.sky.data.local.database.DatabaseFactory
import org.khushhal.sky.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Sky Theatre"
        ) {
            App()
        }
    }
}