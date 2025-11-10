package com.example.basicscodelab

import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    useMetric: MutableState<Boolean>
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    val context = LocalContext.current

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
                    val obj = JSONObject().apply {
                        put("label", widget.label)
                        put("x", widget.position.x)
                        put("y", widget.position.y)
                        put("type", widget.gaugeType.name.lowercase())
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
