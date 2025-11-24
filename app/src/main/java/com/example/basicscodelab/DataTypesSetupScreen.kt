package com.example.basicscodelab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.launch
import com.example.basicscodelab.util.fetchDataTypesFromPi

@Composable
fun DataTypesSetupScreen(
    navController: NavController,
    availableWidgets: SnapshotStateList<String>,
    selectedWidgets: SnapshotStateList<String>,
    standardSet: List<String> = listOf("Speed", "RPM", "Temperature", "FuelLevel")
) {
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Data Types", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Pick how you want to populate your widgets before selecting which to use.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Option 1: Keep current selection
        Button(
            enabled = !busy,
            onClick = { navController.navigate(Screen.WidgetSelection.route) }
        ) {
            Text("Use my current selection")
        }

        // Option 2: Use a standard set
        OutlinedButton(
            enabled = !busy,
            onClick = {
                availableWidgets.clear()
                availableWidgets.addAll(standardSet)
                selectedWidgets.clear() // let the user choose on the next screen still
                navController.navigate(Screen.WidgetSelection.route)
            }
        ) {
            Text("Use standard set (${standardSet.size})")
        }

        // Option 3: Import from Pi
        Button(
            enabled = !busy,
            onClick = {
                scope.launch {
                    busy = true
                    status = "Contacting Pi…"
                    try {
                        val names = fetchDataTypesFromPi(
                            url = "http://10.161.231.41:4000/data-types"
                        )
                        if (names.isNotEmpty()) {
                            availableWidgets.clear()
                            availableWidgets.addAll(names)
                            selectedWidgets.clear() // let the user choose
                            status = "Imported ${names.size} data types"
                            navController.navigate(Screen.WidgetSelection.route)
                        } else {
                            status = "No data types found"
                        }
                    } catch (e: Exception) {
                        status = "Import failed: ${e.message}"
                    } finally {
                        busy = false
                    }
                }
            }
        ) {
            Text(if (busy) "Importing…" else "Import from Pi")
        }

        if (status != null) {
            Text(status!!, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(enabled = !busy, onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
