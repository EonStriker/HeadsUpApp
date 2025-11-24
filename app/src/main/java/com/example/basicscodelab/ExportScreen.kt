package com.example.basicscodelab

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.basicscodelab.util.LockScreenOrientation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import com.example.basicscodelab.util.sendJsonToPi

@Composable
fun ExportScreen(
    navController: NavController,
    widgets: List<Widget>,
    useMetric: MutableState<Boolean>,
    canvasSize: IntSize
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    val context = LocalContext.current

    //resolution for the screen
    val HUD_WIDTH = 1920f
    val HUD_HEIGHT = 1080f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Export Configuration", color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val json = JSONObject().apply {

                val widgetArray = JSONArray()

                widgets.forEach { widget ->

                    // Normalize positions to [0,1]
                    val xNorm = if (canvasSize.width > 0)
                        (widget.position.x / canvasSize.width.toFloat()).coerceIn(0f, 1f)
                    else 0f

                    val yNorm = if (canvasSize.height > 0)
                        (widget.position.y / canvasSize.height.toFloat()).coerceIn(0f, 1f)
                    else 0f

                    // Convert to 1080p coordinates
                    val x1080 = xNorm * HUD_WIDTH
                    val y1080 = yNorm * HUD_HEIGHT

                    val obj = JSONObject().apply {
                        put("label", widget.label)
                        put("x", x1080)
                        put("y", y1080)
                        put("type", widget.gaugeType.name.lowercase())
                        put("color", String.format("#%06X", (widget.colorRGB and 0xFFFFFF)))
                        put("scale", widget.scale)
                        put("numeric", widget.showNumeric)
                    }

                    widgetArray.put(obj)
                }

                put("widgets", widgetArray)
                put("useMetric", if (useMetric.value) 1 else 0)
            }

            CoroutineScope(Dispatchers.IO).launch {
                sendJsonToPi(json, context)
            }

        }) {
            Text("Send to Pi")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
