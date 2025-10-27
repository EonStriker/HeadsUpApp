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
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.clickable
import kotlin.math.floor
import kotlin.math.round

enum class GaugeType { BAR, SWEEPING, NUMBER }


data class Widget(
    val id: Int,
    val label: String,
) {
    var position by mutableStateOf(Offset(50f, 50f))
    var gaugeType by mutableStateOf(GaugeType.SWEEPING)
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

    // make each widget an exact multiple of the grid so edges align
    val cellsPerWidget = 4
    val widgetSizeDp = gridSizeDp * cellsPerWidget
    val widgetSizePx = with(density) { widgetSizeDp.toPx() }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(selectedWidgets) {
        // preserve existing positions and gauge types where labels match
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
            .background(Color.Black)   // draw background first
            .drawGrid(gridPx)          // then draw the grid so it stays visible
    ) {
        widgetStates.forEach { widget ->
            DraggableWidget(
                widget = widget,
                canvasSize = canvasSize,
                gridPx = gridPx,
                widgetSizeDp = widgetSizeDp,
                widgetSizePx = widgetSizePx
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
    gridPx: Float,
    widgetSizeDp: Dp,
    widgetSizePx: Float
) {
    Box(
        modifier = Modifier
            .offset { widget.position.toIntOffset() }
            .size(widgetSizeDp) // exact multiple of the grid
            .background(Color.DarkGray)
            .pointerInput(canvasSize, gridPx) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val next = widget.position + dragAmount
                        widget.position = clampToBounds(next, canvasSize, widgetSizePx)
                    },
                    onDragEnd = {
                        widget.position = snapToGridClamped(
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
        // Center label
        Text(
            widget.label,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .background(Color(0x66000000), shape = MaterialTheme.shapes.small)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable {
                    val all = GaugeType.values()
                    val i = all.indexOf(widget.gaugeType)
                    widget.gaugeType = all[(i + 1) % all.size]
                }
        ) {
            Text(
                text = widget.gaugeType.name.lowercase(), // "bar" / "sweeping" / "number"
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
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

private fun snapToGridClamped(
    p: Offset,
    gridPx: Float,
    canvas: IntSize,
    sizePx: Float
): Offset {
    if (gridPx <= 0f) return clampToBounds(p, canvas, sizePx)
    val maxStepsX = floor((canvas.width - sizePx) / gridPx).toInt().coerceAtLeast(0)
    val maxStepsY = floor((canvas.height - sizePx) / gridPx).toInt().coerceAtLeast(0)
    val stepX = (p.x / gridPx).roundToInt().coerceIn(0, maxStepsX)
    val stepY = (p.y / gridPx).roundToInt().coerceIn(0, maxStepsY)
    return Offset(stepX * gridPx, stepY * gridPx)
}

private fun Modifier.drawGrid(gridPx: Float): Modifier = drawBehind {
    if (gridPx <= 0f) return@drawBehind

    // vertical lines
    val cols = floor(size.width / gridPx).toInt()
    for (i in 0..cols) {
        val x = i * gridPx
        val xi = round(x)
        drawLine(
            color = Color(0x44FFFFFF),
            start = Offset(xi, 0f),
            end = Offset(xi, size.height),
            strokeWidth = 1f
        )
    }
    // horizontal lines
    val rows = floor(size.height / gridPx).toInt()
    for (j in 0..rows) {
        val y = j * gridPx
        val yj = round(y)
        drawLine(
            color = Color(0x44FFFFFF),
            start = Offset(0f, yj),
            end = Offset(size.width, yj),
            strokeWidth = 1f
        )
    }
}
