package com.example.basicscodelab

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
fun WidgetSelectionScreen(
    navController: NavController,
    availableWidgets: List<String>,
    selectedWidgets: SnapshotStateList<String>,
    onWidgetsSelected: (List<String>) -> Unit
) {
    val scrollState = rememberScrollState()

    // Local selection state copied from selectedWidgets
    val currentSelection = remember { mutableStateListOf<String>() }

    // Sync initial state with passed selections
    LaunchedEffect(selectedWidgets) {
        currentSelection.clear()
        currentSelection.addAll(selectedWidgets)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Your Widgets",
            style = MaterialTheme.typography.headlineSmall
        )

        if (availableWidgets.isEmpty()) {
            Text(
                "No widgets available. Try going back and importing data types first.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            // list available widget checkboxes
            availableWidgets.forEach { widgetName ->
                val isChecked = currentSelection.contains(widgetName)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (!currentSelection.contains(widgetName))
                                    currentSelection.add(widgetName)
                            } else {
                                currentSelection.remove(widgetName)
                            }
                        }
                    )
                    Text(
                        text = widgetName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue button
        Button(
            onClick = {
                onWidgetsSelected(currentSelection)
                navController.navigate(Screen.Layout.route)
            },
            enabled = availableWidgets.isNotEmpty()
        ) {
            Text("Continue to Layout")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Back button
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}