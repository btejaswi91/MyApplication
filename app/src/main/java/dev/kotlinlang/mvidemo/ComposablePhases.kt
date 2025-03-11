package dev.kotlinlang.mvidemo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

// Track measure phase (Blue)
fun Modifier.trackMeasure(tag: String): Modifier = layout { measurable, constraints ->
    Log.d(tag, "🟦 Measure Phase")
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.place(0, 0)
    }
}

// Track layout/placement phase (Green)
fun Modifier.trackLayout(tag: String): Modifier = layout { measurable, constraints ->
    Log.d(tag, "🟩 Layout Phase")
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        Log.d(tag, "🟩 Placement Phase")
        placeable.place(0, 0)
    }
}

// Track drawing phase (Red)
fun Modifier.trackDraw(tag: String): Modifier = drawWithContent {
    Log.d(tag, "🟥 Drawing Phase")
    drawContent()
}


@Composable
fun EfficientBox() {
    var color by remember { mutableStateOf(Color.Red) }

    // Track recomposition (Yellow)
    val recomposeColor by rememberUpdatedState(if (color == Color.Red) Color.Yellow else Color.Transparent)

    Box(
        modifier = Modifier
            .trackMeasure("Efficient") // 🟦
            .trackLayout("Efficient")  // 🟩
            .size(100.dp)
            .background(recomposeColor) // 🟨 (Recomposition)
            .background(color)          // 🟥 (Drawing)
            .trackDraw("Efficient")    // 🟥
            .clickable { color = Color.Blue }
    )
}

@Composable
fun InefficientBox() {
    var color by remember { mutableStateOf(Color.Red) }

    // Track recomposition (Yellow)
    val recomposeColor by rememberUpdatedState(if (color == Color.Red) Color.Yellow else Color.Transparent)

    Box(
        modifier = Modifier
            .clickable { color = Color.Blue }
            .trackMeasure("Inefficient") // 🟦
            .trackLayout("Inefficient")  // 🟩
            .size(100.dp)
            .background(recomposeColor)   // 🟨 (Recomposition)
            .background(color)           // 🟥 (Drawing)
            .trackDraw("Inefficient")    // 🟥
    )
}