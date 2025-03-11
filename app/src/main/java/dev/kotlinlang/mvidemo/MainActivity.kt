package dev.kotlinlang.mvidemo


import ItemsListWithKeys
import ItemsListWithoutKeys
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            LayoutExample()
            DrawingExample()
        }
    }
}

@Composable
fun LayoutExample() {
    var isShown: Boolean by remember {
        mutableStateOf(value = false)
    }

    val offset: Int by animateIntAsState(if (isShown) 0 else 300, animationSpec = tween())

    Column(
        modifier = Modifier
    ) {
        Button(
            onClick = {
                isShown = !isShown
            }
        ) {
            Text(text = "Toggle Text")
        }

        // Unoptimized
//        Text(
//            text = "Hello #1",
//            modifier = Modifier.offset(x = offset.dp, y = offset.dp)
//        )

        // Optimized
        Text(
            text = "Hello #2",
            modifier = Modifier.offset {
                // The `offsetX` state is read in the placement step
                // of the layout phase when the offset is calculated.
                // Changes in `offsetX` restart the layout.
                IntOffset(offset, offset)
            }
        )
    }
}

@Composable
fun DrawingExample() {
    var isShown: Boolean by remember {
        mutableStateOf(value = false)
    }

    val opacity: Float by animateFloatAsState(if (isShown) 1f else 0.5f, animationSpec = tween())

    Column(
        modifier = Modifier
    ) {
        Button(
            onClick = {
                isShown = !isShown
            }
        ) {
            Text(text = "Toggle Text")
        }

        // Unoptimized
//        Text(
//            text = "Hello World! #1",
//            modifier = Modifier.alpha(opacity)
//        )

        // Optimized
        Text(
            text = "Hello World! #2",
            modifier = Modifier.graphicsLayer { alpha = opacity }
        )
    }
}
