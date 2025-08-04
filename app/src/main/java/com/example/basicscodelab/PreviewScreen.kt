package com.example.basicscodelab

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.basicscodelab.util.LockScreenOrientation
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun PreviewScreen(navController: NavController, widgets: List<Widget>) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        widgets.forEach { widget ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(widget.position.x.roundToInt(), widget.position.y.roundToInt()) }
                    .size(100.dp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(widget.label, color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }

            Button(onClick = { navController.navigate(Screen.Export.route) }) {
                Text("Continue to Export")
            }
        }
    }
}
