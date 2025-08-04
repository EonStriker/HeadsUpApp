package com.example.basicscodelab

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import android.content.pm.ActivityInfo
import com.example.basicscodelab.util.LockScreenOrientation

data class Widget(
    val id: Int,
    val label: String,
    var position: Offset = Offset(50f, 50f)
)

@Composable
fun LayoutScreen(
    navController: NavController,
    selectedWidgets: List<String>,
    widgetStates: MutableList<Widget>
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    LaunchedEffect(selectedWidgets) {
        widgetStates.clear()
        selectedWidgets.forEachIndexed { index, label ->
            widgetStates.add(Widget(id = index, label = label))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        widgetStates.forEach { widget ->
            DraggableWidget(widget)
        }

        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { navController.navigate(Screen.Preview.route) }) {
                Text("Preview")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
    }
}

@Composable
fun DraggableWidget(widget: Widget) {
    var offset by remember { mutableStateOf(widget.position) }

    Box(
        modifier = Modifier
            .offset { offset.toIntOffset() }
            .size(100.dp)
            .background(Color.DarkGray)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                    widget.position = offset
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            widget.label,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
