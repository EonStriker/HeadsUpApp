package com.example.basicscodelab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ExportScreen(navController: NavController) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Export Configuration", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Generated JSON:", style = MaterialTheme.typography.labelLarge)


        Button(onClick = { navController.navigate(Screen.Welcome.route) }) {
            Text("Finish and Return Home")
        }
    }
}
