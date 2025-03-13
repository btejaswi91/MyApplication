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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CartScreen()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    onNavigateToCheckout: () -> Unit = {
        Log.i("CartScreen", "Navigate to checkout")
    }
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CartEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                CartEffect.NavigateToCheckout -> {
                    onNavigateToCheckout()
                }
            }
        }
    }

    // Load cart on first composition
    LaunchedEffect(Unit) {
        viewModel.processIntent(CartIntent.LoadCart)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            CenterAlignedTopAppBar(
                title = { Text("Shopping Cart") }
            )

            // Content
            Box(modifier = Modifier.weight(1f)) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.errorMessage != null -> {
                        ErrorState(
                            message = state.errorMessage!!,
                            onRetry = { viewModel.processIntent(CartIntent.LoadCart) }
                        )
                    }

                    else -> {
                        CartContent(
                            state = state,
                            onRemoveItem = { itemId ->
                                viewModel.processIntent(CartIntent.RemoveItem(itemId))
                            }
                        )
                    }
                }
            }

            // Bottom Bar
            if (state.items.isNotEmpty()) {
                CartBottomBar(state, viewModel)
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CartContent(
    state: CartState,
    onRemoveItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(state.items, key = { it.id }) { item ->
            CartItemRow(
                item = item,
                onRemove = { onRemoveItem(item.id) }
            )
            Divider()
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text("Quantity: ${item.quantity}")
            Text("$${"%.2f".format(item.price)} each")
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove item")
        }
    }
}

@Composable
private fun CartBottomBar(
    state: CartState,
    viewModel: CartViewModel
) {
    val totalPrice = state.items.sumOf { it.price * it.quantity }

    Surface(tonalElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: $${"%.2f".format(totalPrice)}",
                style = MaterialTheme.typography.titleLarge
            )

            Button(onClick = {
                // Use processIntent instead of directly calling emitEffect
                // We would need a new intent type for checkout
                viewModel.processIntent(CartIntent.Checkout)
            }) {
                Text("Checkout")
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
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
