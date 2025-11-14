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
import androidx.compose.ui.zIndex

enum class GaugeType { BAR, SWEEPING, NUMBER }

val widgetPalette = listOf(
    0xFFFFFFFF.toInt(), // white
    0xFFFF5722.toInt(), // deep orange
    0xFF4CAF50.toInt(), // green
    0xFF03A9F4.toInt(), // light blue
    0xFFFFEB3B.toInt()  // yellow
)

val sizeSteps = listOf(0.75f, 1.0f, 1.25f)

data class Widget(
    val id: Int,
    val label: String,
) {
    var position by mutableStateOf(Offset(50f, 50f))
    var gaugeType by mutableStateOf(GaugeType.SWEEPING)
    var colorRGB by mutableStateOf(0xFFFFFFFF.toInt())   // default white
    var scale by mutableStateOf(1.0f)
    var showNumeric by mutableStateOf(false)
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

    // selection
    var selectedId by remember { mutableStateOf<Int?>(null) }

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
        // clear selection if it no longer exists
        if (selectedId != null && widgetStates.none { it.id == selectedId }) {
            selectedId = null
        }
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
                widgetSizePx = widgetSizePx,
                selected = selectedId == widget.id,
                onSelect = { selectedId = widget.id }
            )
        }

        // floating toolbar for the selected widget (Type / Color / Size / Numeric)
        val sel = remember(selectedId, widgetStates) {
            widgetStates.firstOrNull { it.id == selectedId }
        }
        if (sel != null) {
            val barHeightDp = 32.dp
            val barPadDp = 6.dp
            val barWidthPx = 260 // slightly wider to fit checkbox
            val liftPx = with(density) { (barHeightDp + barPadDp).toPx() }
            val x = sel.position.x.roundToInt()
            val y = (sel.position.y - liftPx).roundToInt()
            val clampedX = x.coerceIn(0, canvasSize.width - barWidthPx)
            val clampedY = y.coerceAtLeast(0)

            Box(
                modifier = Modifier
                    .offset { IntOffset(clampedX, clampedY) }
                    .background(Color(0xAA000000), shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .zIndex(2f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        sel.gaugeType.name.lowercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.clickable {
                            val all = GaugeType.values()
                            val i = all.indexOf(sel.gaugeType)
                            sel.gaugeType = all[(i + 1) % all.size]
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .size(14.dp)
                            .background(Color(sel.colorRGB), shape = MaterialTheme.shapes.extraSmall)
                            .clickable {
                                val idx = widgetPalette.indexOf(sel.colorRGB).let { if (it < 0) 0 else it }
                                sel.colorRGB = widgetPalette[(idx + 1) % widgetPalette.size]
                            }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        when (sel.scale) {
                            0.75f -> "S"
                            1.0f -> "M"
                            1.25f -> "L"
                            else -> "${(sel.scale * 100).toInt()}%"
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.clickable {
                            val idx = sizeSteps.indexOfFirst { it == sel.scale }.let { if (it < 0) 1 else it }
                            sel.scale = sizeSteps[(idx + 1) % sizeSteps.size]
                            // snap to grid after resize
                            sel.position = snapToGridClamped(sel.position, gridPx, canvasSize, widgetSizePx * sel.scale)
                        }
                    )
                    // only show numeric checkbox when gauge is not NUMBER
                    if (sel.gaugeType != GaugeType.NUMBER) {
                        Spacer(Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = sel.showNumeric,
                                onCheckedChange = { sel.showNumeric = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.White,
                                    uncheckedColor = Color.White,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text(
                                "num",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
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
    widgetSizePx: Float,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val scaledSizeDp = widgetSizeDp * widget.scale
    val scaledSizePx = widgetSizePx * widget.scale

    Box(
        modifier = Modifier
            .offset { widget.position.toIntOffset() }
            .size(scaledSizeDp) // exact multiple of the grid (scaled)
            .background(Color.DarkGray)
            .pointerInput(canvasSize, gridPx, widget.scale) {
                detectDragGestures(
                    onDragStart = { onSelect() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val next = widget.position + dragAmount
                        widget.position = clampToBounds(next, canvasSize, scaledSizePx)
                    },
                    onDragEnd = {
                        widget.position = snapToGridClamped(
                            widget.position,
                            gridPx,
                            canvasSize,
                            scaledSizePx
                        )
                    }
                )
            }
            .clickable { onSelect() }
            .zIndex(if (selected) 1f else 0f),
        contentAlignment = Alignment.Center
    ) {
        // Center label
        Text(
            widget.label,
            color = Color(widget.colorRGB),
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
