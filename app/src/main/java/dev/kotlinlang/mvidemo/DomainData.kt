package dev.kotlinlang.mvidemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Data classes for cart items and state
data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

sealed class CartIntent {
    object LoadCart : CartIntent()
    data class RemoveItem(val itemId: String) : CartIntent()
}

data class CartState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class CartEffect {
    data class ShowToast(val message: String) : CartEffect()
    object NavigateToCheckout : CartEffect()
}

// Base ViewModel with Middleware support
abstract class BaseViewModel<Intent, State, Effect> : ViewModel() {
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<State> get() = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> get() = _effect.asSharedFlow()

    protected abstract fun initialState(): State

    fun processIntent(intent: Intent) {
        viewModelScope.launch(Dispatchers.Default) {
            val processedIntent = middleware(intent)
            if (processedIntent != null) {
                handleIntent(processedIntent)
            }
        }
    }

    protected open fun middleware(intent: Intent): Intent? = intent

    protected abstract suspend fun handleIntent(intent: Intent)

    protected suspend fun updateState(newState: State) {
        withContext(Dispatchers.Main) {
            _state.value = newState
        }
    }

    protected suspend fun emitEffect(effect: Effect) {
        withContext(Dispatchers.Main) {
            _effect.emit(effect)
        }
    }
}

// CartViewModel extending BaseViewModel
class CartViewModel : BaseViewModel<CartIntent, CartState, CartEffect>() {
    private val fakeCartRepository = FakeCartRepository()

    override fun initialState() = CartState()

    override fun middleware(intent: CartIntent): CartIntent? {
        return when (intent) {
            is CartIntent.RemoveItem -> {
                if (state.value.items.any { it.id == intent.itemId }) intent else null
            }
            else -> intent
        }
    }

    override suspend fun handleIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.LoadCart -> loadCart()
            is CartIntent.RemoveItem -> removeItem(intent.itemId)
        }
    }

    private suspend fun loadCart() {
        updateState(state.value.copy(isLoading = true))
        try {
            val items = withContext(Dispatchers.IO) {
                delay(1500)
                fakeCartRepository.getCartItems()
            }
            updateState(state.value.copy(items = items, isLoading = false))
        } catch (e: Exception) {
            updateState(state.value.copy(isLoading = false, errorMessage = "Failed to load cart items"))
            emitEffect(CartEffect.ShowToast("Error: ${e.message}"))
        }
    }

    private suspend fun removeItem(itemId: String) {
        updateState(state.value.copy(items = state.value.items.filter { it.id != itemId }))
        emitEffect(CartEffect.ShowToast("Item removed"))
    }
}

class FakeCartRepository {
    suspend fun getCartItems(): List<CartItem> {
        return listOf(
            CartItem("1", "Wireless Headphones", 199.99, 1, "https://example.com/headphones.jpg"),
            CartItem("2", "Smart Watch", 299.99, 1, "https://example.com/watch.jpg"),
            CartItem("3", "Bluetooth Speaker", 129.99, 2, "https://example.com/speaker.jpg")
        )
    }
}
