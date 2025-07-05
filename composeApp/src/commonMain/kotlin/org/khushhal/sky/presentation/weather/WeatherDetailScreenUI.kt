package org.khushhal.sky.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.*
import org.khushhal.sky.domain.model.Weather
import org.khushhal.sky.presentation.preference.PreferenceViewModel
import org.khushhal.sky.presentation.search.WeatherSearchViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreenUI(
    preferenceViewModel: PreferenceViewModel = remember { PreferenceViewModel() },
    lat: Double,
    lon: Double,
    onBack: () -> Unit
) {
    val viewModel: WeatherDetailViewModel = koinViewModel()
    val preferenceState by preferenceViewModel.state.collectAsState()
    val selectedUnit = preferenceState.selectedUnit
    val state by viewModel.state.collectAsState()

    LaunchedEffect(selectedUnit) {
        viewModel.fetchWeather(lat, lon, selectedUnit)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D367D),
                        Color(0xFF576473),
                        Color(0xFF72809D)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "<  ",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.clickable { onBack() }
                            )
                            Text(
                                text = state.weather?.name ?: "Unknown",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    state.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Error: ${state.error}",
                                color = Color.White
                            )
                        }
                    }

                    state.weather != null -> {
                        WeatherGridWithHeader(
                            weather = state.weather!!,
                            unit = selectedUnit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun WeatherGridWithHeader(weather: Weather, unit: String, modifier: Modifier = Modifier) {
    val timeZone = TimeZone.currentSystemDefault()

    val tempUnit = when (unit) {
        "imperial" -> "°F"
        "standard" -> "K"
        else -> "°C"
    }

    val pressureUnit = if (unit == "imperial") "inHg" else "hPa"
    val pressureValue = if (unit == "imperial") {
        weather.pressure?.let {
            val converted = (it * 0.02953 * 100).roundToInt() / 100.0
            "$converted $pressureUnit"
        } ?: "-- $pressureUnit"
    } else {
        "${weather.pressure ?: "--"} $pressureUnit"
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item(span = { GridItemSpan(2) }) {
            WeatherHeader(
                city = weather.name ?: "Unknown",
                description = weather.description ?: "N/A",
                iconUrl = weather.icon ?: "01d",
                temp = weather.temp,
                feelsLike = weather.feelsLike,
                tempMin = weather.tempMin,
                tempMax = weather.tempMax,
                unit = unit
            )
        }

        item { InfoCard("Sunrise", unixToLocalTime(weather.sunrise, timeZone), RoundedCornerShape( 24.dp),
            emoji = "☀️", emojiAlignment = Alignment.TopStart) }
        item { InfoCard("Sunset", unixToLocalTime(weather.sunset, timeZone), RoundedCornerShape(24.dp),
            emoji = "🌙", emojiAlignment = Alignment.BottomEnd) }
        item { InfoCard("Condition", weather.description ?: "Unknown", RoundedCornerShape(24.dp)) }
        item { InfoCard("Pressure", pressureValue, CircleShape , emoji = "🌡️", emojiAlignment = Alignment.Center)}
        item { InfoCard("Humidity", "${weather.humidity ?: "--"}%", CutCornerShape(100.dp),
            emoji = "💧", emojiAlignment = Alignment.Center) }

        weather.seaLevel?.let {
            item { InfoCard("Sea Level", "$it hPa", RoundedCornerShape( 24.dp)) }
        }

        weather.gust?.let {
            val gustSpeed = if (unit == "imperial") "${(it * 2.237).roundToInt()} mph" else "${it.roundToInt()} m/s"
            item { InfoCard("Gust", gustSpeed, RoundedCornerShape( 24.dp),
                emoji = "💨", emojiAlignment = Alignment.TopStart) }
        }

        weather.deg?.let {
            item { InfoCard("Wind Dir", "$it°", RoundedCornerShape(24.dp),
                emoji = "🧭", emojiAlignment = Alignment.BottomEnd) }
        }
    }
}


@Composable
fun WeatherHeader(
    city: String,
    description: String,
    iconUrl: String,
    temp: Double?,
    feelsLike: Double?,
    tempMin: Double?,
    tempMax: Double?,
    unit: String
) {
    val tempUnit = when (unit) {
        "imperial" -> "°F"
        "standard" -> "K"
        else -> "°C"
    }

    val displayTemp = temp?.toInt()?.toString() ?: "--"
    val displayFeels = feelsLike?.toInt()?.toString() ?: "--"
    val displayMin = tempMin?.toInt()?.toString() ?: "--"
    val displayMax = tempMax?.toInt()?.toString() ?: "--"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row (verticalAlignment = Alignment.CenterVertically) {




            Text(
                text = "$displayTemp$tempUnit",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )

            KamelImage(
                resource = asyncPainterResource("https://openweathermap.org/img/wn/${iconUrl}@4x.png"),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Feels like $displayFeels$tempUnit", style = MaterialTheme.typography.bodyLarge,
            color = Color.White)
        Text("High $displayMax$tempUnit  ·  Low $displayMin$tempUnit", style = MaterialTheme.typography.bodyMedium,
            color = Color.White)
    }
}




@Composable
fun InfoCard(
    title: String,
    value: String,
    shape: Shape,
    innerBoxColor: Color = Color(0xFF324B4D),
    emoji: String? = null,
    emojiAlignment: Alignment? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121414)
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .clip(shape)
                .background(innerBoxColor)
                .padding(8.dp)
        ) {
            if (emojiAlignment != null) {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = emojiAlignment) {
                    if (emoji != null) {
                        Text(emoji, style = MaterialTheme.typography.displayMedium)
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }
        }
    }
}



fun unixToLocalTime(unix: Int?, timeZone: TimeZone): String {
    return unix?.let {
        try {
            val instant = Instant.fromEpochSeconds(it.toLong())
            val local = instant.toLocalDateTime(timeZone)
            val hour = local.hour
            val minute = local.minute
            val amPm = if (hour >= 12) "PM" else "AM"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12
            "${hour12}:${minute.toString().padStart(2, '0')} $amPm"
        } catch (e: Exception) {
            "--:-- --"
        }
    } ?: "--:-- --"
}
