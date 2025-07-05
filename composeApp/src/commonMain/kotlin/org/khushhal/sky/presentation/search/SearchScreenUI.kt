package org.khushhal.sky.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.khushhal.sky.domain.model.City
import org.khushhal.sky.presentation.preference.PreferenceHolder
import org.khushhal.sky.util.toCity
import org.khushhal.sky.util.toCityEntity
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenUI(
    onCitySelected: (city: City) -> Unit,
    onSettingsClick: () -> Unit
) {
    val viewModel: WeatherSearchViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val units = PreferenceHolder.unit



    LaunchedEffect(Unit) {
        viewModel.loadAndRefreshSavedCities(units)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sky Theatre", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    Text(
                        text = " ⚙️",
                        modifier = Modifier
                            .clickable { onSettingsClick() }
                            .padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {


            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChanged(it) },
                label = { Text("Search for a location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChanged("") }) {
                            Text(text = "⨯",
                                style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
            )


            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
            }

            state.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }




            if (state.suggestions.isNotEmpty()) {
                Text(
                    text = "Suggestions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyColumn {
                    items(state.suggestions) { city ->
                        CitySuggestionItem(
                            cityName = city.name ?: "Unknown",
                            state = city.state ?: "",
                            country = city.country ?: "XX",
                            onClick = { onCitySelected(city) },
                            onAddClick = {
                                viewModel.saveCity(city.toCityEntity())
                            }
                        )
                    }
                }
            }


            if (state.savedCities.isNotEmpty() && state.suggestions.isEmpty()) {
                Text(
                    text = "📍Saved locations",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyColumn {
                    items(state.savedCities, key = { it.key }) { savedCity ->
                        var visible by remember { mutableStateOf(true) }

                        AnimatedVisibility(visible = visible) {
                            SavedCityItem(
                                cityName = savedCity.name,
                                temp = savedCity.temp,
                                description = savedCity.description,
                                iconUrl = savedCity.icon,
                                units = units,
                                onClick = { onCitySelected(savedCity.toCity()) },
                                onLongPress = {
                                    visible = false
                                    viewModel.deleteCity(savedCity.key)
                                }
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun CitySuggestionItem(
    cityName: String,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    state: String,
    country: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() }
        ) {
            Text(text = cityName, style = MaterialTheme.typography.titleMedium)
            val locationText = if (state.isNotEmpty()) "$state, $country" else country
            Text(
                text = locationText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextButton(
            onClick = onAddClick
        ){
            Text(text = "+",
                style = MaterialTheme.typography.headlineMedium)
        }
    }

    HorizontalDivider(
        Modifier.padding(top = 8.dp),
        thickness = DividerDefaults.Thickness,
        color = DividerDefaults.color
    )
}



@Composable
fun SavedCityItem(
    cityName: String,
    temp: Double?,
    description: String?,
    iconUrl: String?,
    units: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val showTemp = when (units) {
        "imperial" -> "${temp?.toInt() ?: "--"}°F"
        "standard" -> "${temp?.toInt() ?: "--"}K"
        "metric" -> "${temp?.toInt() ?: "--"}°C"
        else -> "--"
    }


    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(50),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                    onLongClick = onLongPress
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                iconUrl?.let {
                    KamelImage(
                        resource = asyncPainterResource(data = "https://openweathermap.org/img/wn/${it}@2x.png"),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description ?: "No description",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = showTemp,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}



