package org.khushhal.sky.presentation.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreenUI(
    viewModel: PreferenceViewModel = remember { PreferenceViewModel() },
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val themeOptions = AppTheme.entries
    val unitOptions = listOf("Metric", "Imperial", "Standard")

    var showUnitSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "<  ",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.clickable { onBack() }
                )

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(Modifier.height(16.dp))


            PreferenceRow(
                label = "Temperature",
                value = state.selectedUnit.replaceFirstChar { it.uppercase() },
                onClick = { showUnitSheet = true }
            )

            Spacer(Modifier.height(24.dp))


            PreferenceRow(
                label = "Theme",
                value = state.selectedTheme,
                onClick = { showThemeSheet = true }
            )
        }


        if (showUnitSheet) {
            ModalBottomSheet(onDismissRequest = { showUnitSheet = false }) {
                BottomSheetOptionSelector(
                    title = "Temperature",
                    options = unitOptions,
                    selectedOption = state.selectedUnit.replaceFirstChar { it.uppercase() },
                    onOptionSelected = {
                        viewModel.updateUnit(it.lowercase())
                        showUnitSheet = false
                    }
                )
            }
        }


        if (showThemeSheet) {
            ModalBottomSheet(onDismissRequest = { showThemeSheet = false }) {
                BottomSheetOptionSelector(
                    title = "Theme",
                    options = themeOptions.map { it.name },
                    selectedOption = state.selectedTheme,
                    onOptionSelected = {
                        viewModel.updateTheme(it)
                        ThemeController.updateTheme(AppTheme.valueOf(it))
                        showThemeSheet = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PreferenceRow(label: String, value: String, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(vertical = 12.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun BottomSheetOptionSelector(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 12.dp)
            ) {
                RadioButton(
                    selected = option.equals(selectedOption, ignoreCase = true),
                    onClick = { onOptionSelected(option) }
                )
                Spacer(Modifier.width(12.dp))
                Text(option, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = { onOptionSelected(selectedOption) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Done")
        }
    }
}
