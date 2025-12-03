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

    // match LayoutScreen base size: gridSizeDp (24.dp) * cellsPerWidget (4)
    val baseSizeDp = 24.dp * 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        widgets.forEach { widget ->
            val scaledSizeDp = baseSizeDp * widget.scale

            Box(
                modifier = Modifier
                    .offset { IntOffset(widget.position.x.roundToInt(), widget.position.y.roundToInt()) }
                    .size(scaledSizeDp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        widget.label,
                        color = Color(widget.colorRGB)
                    )

                    // show numeric preview only when enabled and not a NUMBER gauge
                    if (widget.showNumeric && widget.gaugeType != GaugeType.NUMBER) {
                        Spacer(modifier = Modifier.height(4.dp))
                        // placeholder numeric value for preview to show it indicates. maybe change later
                        Text(
                            "000",
                            color = Color(widget.colorRGB),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
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