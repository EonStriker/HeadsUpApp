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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.draw.drawBehind

data class Widget(
    val id: Int,
    val label: String,
) {
    var position by mutableStateOf(Offset(50f, 50f))
}

@Composable
fun LayoutScreen(
    navController: NavController,
    selectedWidgets: List<String>,
    widgetStates: MutableList<Widget>
) {
    // grid settings
    val gridSizeDp = 24.dp
    val density = LocalDensity.current
    val gridPx = with(density) { gridSizeDp.toPx() }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(selectedWidgets) {
        // preserve existing positions where labels match
        val byLabel = widgetStates.associateBy { it.label }.toMutableMap()
        val newList = mutableListOf<Widget>()
        selectedWidgets.forEachIndexed { index, label ->
            val existing = byLabel.remove(label)
            if (existing != null) {
                newList.add(existing)
            } else {
                newList.add(Widget(id = index, label = label))
            }
        }
        widgetStates.clear()
        widgetStates.addAll(newList)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .background(Color.Black)   // background first
            .drawGrid(gridPx)          // grid on top so it's visible
    ) {
        widgetStates.forEach { widget ->
            DraggableWidget(
                widget = widget,
                canvasSize = canvasSize,
                gridPx = gridPx
            )
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
fun DraggableWidget(
    widget: Widget,
    canvasSize: IntSize,
    gridPx: Float
) {
    val widgetSizeDp = 100.dp
    val density = LocalDensity.current
    val widgetSizePx = with(density) { widgetSizeDp.toPx() }

    Box(
        modifier = Modifier
            .offset { widget.position.toIntOffset() }
            .size(widgetSizeDp)
            .background(Color.DarkGray)
            .pointerInput(canvasSize, gridPx) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val next = widget.position + dragAmount
                        widget.position = clampToBounds(next, canvasSize, widgetSizePx)
                    },
                    onDragEnd = {
                        widget.position = snapToGrid(
                            widget.position,
                            gridPx,
                            canvasSize,
                            widgetSizePx
                        )
                    }
                )
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

private fun clampToBounds(p: Offset, canvas: IntSize, sizePx: Float): Offset {
    val maxX = (canvas.width - sizePx).coerceAtLeast(0f)
    val maxY = (canvas.height - sizePx).coerceAtLeast(0f)
    val x = p.x.coerceIn(0f, maxX)
    val y = p.y.coerceIn(0f, maxY)
    return Offset(x, y)
}

private fun snapToGrid(p: Offset, gridPx: Float, canvas: IntSize, sizePx: Float): Offset {
    if (gridPx <= 0f) return p
    val snappedX = (p.x / gridPx).roundToInt() * gridPx
    val snappedY = (p.y / gridPx).roundToInt() * gridPx
    return clampToBounds(Offset(snappedX, snappedY), canvas, sizePx)
}

private fun Modifier.drawGrid(gridPx: Float): Modifier = drawBehind {
    if (gridPx <= 0f) return@drawBehind
    var x = 0f
    while (x <= size.width) {
        drawLine(
            color = Color(0x44FFFFFF), // slightly brighter than before
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
        x += gridPx
    }
    var y = 0f
    while (y <= size.height) {
        drawLine(
            color = Color(0x44FFFFFF),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
        y += gridPx
    }
}
