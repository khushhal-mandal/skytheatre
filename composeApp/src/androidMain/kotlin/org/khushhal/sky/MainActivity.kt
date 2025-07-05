package org.khushhal.sky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.khushhal.sky.data.local.database.DatabaseFactory
import org.khushhal.sky.di.initKoin
import org.khushhal.sky.domain.repository.CityRepository
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(this@MainActivity)  // Only Android needs context
        }

        setContent {
            App()
        }
    }
}

