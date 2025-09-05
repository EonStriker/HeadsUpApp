package com.example.basicscodelab

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun WidgetSelectionScreen(
    navController: NavController,
    selectedWidgets: List<String>,
    onWidgetsSelected: (List<String>) -> Unit
) {
    val widgetOptions = listOf("Speed", "RPM", "Temperature", "Fuel", "Battery Voltage", "Coolant Temp")

    // Track which checkboxes are selected
    val selections = remember {
        mutableStateMapOf<String, Boolean>().apply {
            widgetOptions.forEach { widget ->
                this[widget] = selectedWidgets.contains(widget)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())  // <-- Add this
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

    Text("Select Widgets", style = MaterialTheme.typography.headlineSmall)

        widgetOptions.forEach { widget ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = selections[widget] == true,
                        onValueChange = { selections[widget] = it }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = widget)
                Checkbox(
                    checked = selections[widget] == true,
                    onCheckedChange = null // Handled by toggleable
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    val selected = selections.filterValues { it }.keys.toList()
                    onWidgetsSelected(selected)
                    navController.navigate(Screen.Layout.route)
                },
                enabled = selections.containsValue(true)
            ) {
                Text("Next")
            }
        }
    }
}
