package com.example.basicscodelab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// , isDarkTheme: Boolean, onThemeToggle: () -> Unit
@Composable
fun SettingsScreen(navController: NavController) {
    var useMetric by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle: Units
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Use Metric Units")
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = useMetric, onCheckedChange = { useMetric = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle: Dark Theme
        /*Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dark Theme", modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onThemeToggle() }
            )
        }*/

        // Spacer(modifier = Modifier.height(32.dp))

        // Back Button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

