package dev.kotlinlang.mvidemo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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




class CartViewModel : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CartEffect>()
    val effect: SharedFlow<CartEffect> = _effect.asSharedFlow()

    private val fakeCartRepository = FakeCartRepository()

    fun processIntent(intent: CartIntent) {
        viewModelScope.launch {
            when (intent) {
                is CartIntent.LoadCart -> loadCart()
                is CartIntent.RemoveItem -> removeItem(intent.itemId)
            }
        }
    }

    private suspend fun loadCart() {
        _state.value = _state.value.copy(isLoading = true)
        try {
            val items = withContext(Dispatchers.IO) {
                // Simulate network call
                delay(1500)
                fakeCartRepository.getCartItems()
            }
            _state.value = _state.value.copy(
                items = items,
                isLoading = false,
                errorMessage = null
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Failed to load cart items"
            )
            _effect.emit(CartEffect.ShowToast("Error: ${e.message}"))
        }
    }

    private suspend fun removeItem(itemId: String) {
        _state.value = _state.value.copy(
            items = _state.value.items.filter { it.id != itemId }
        )
        _effect.emit(CartEffect.ShowToast("Item removed"))
    }
}

class FakeCartRepository {
    suspend fun getCartItems(): List<CartItem> {
        return listOf(
            CartItem(
                id = "1",
                name = "Wireless Headphones",
                price = 199.99,
                quantity = 1,
                imageUrl = "https://m.media-amazon.com/images/I/51IQ+xDrilL._AC_UL640_FMwebp_QL65_.jpg"
            ),
            CartItem(
                id = "2",
                name = "Smart Watch",
                price = 299.99,
                quantity = 1,
                imageUrl = "https://m.media-amazon.com/images/I/51rI+jSqx5L._SY879_.jpg"
            ),
            CartItem(
                id = "3",
                name = "Bluetooth Speaker",
                price = 129.99,
                quantity = 2,
                imageUrl = "https://example.com/speaker.jpg"
            ),
            CartItem(
                id = "11",
                name = "Wireless Headphones",
                price = 199.99,
                quantity = 1,
                imageUrl = "https://m.media-amazon.com/images/I/51IQ+xDrilL._AC_UL640_FMwebp_QL65_.jpg"
            ),
            CartItem(
                id = "21",
                name = "Smart Watch",
                price = 299.99,
                quantity = 1,
                imageUrl = "https://m.media-amazon.com/images/I/51rI+jSqx5L._SY879_.jpg"
            ),
            CartItem(
                id = "7",
                name = "Bluetooth Speaker",
                price = 129.99,
                quantity = 2,
                imageUrl = "https://example.com/speaker.jpg"
            ),
            CartItem(
                id = "4",
                name = "Wireless Headphones",
                price = 199.99,
                quantity = 1,
                imageUrl = "https://m.media-amazon.com/images/I/51IQ+xDrilL._AC_UL640_FMwebp_QL65_.jpg"
            ),
            CartItem(
                id = "5",
                name = "Smart Watch",
                price = 299.99,
                quantity = 1,
                imageUrl = "https://m.media-amazon.com/images/I/51rI+jSqx5L._SY879_.jpg"
            ),
            CartItem(
                id = "6",
                name = "Bluetooth Speaker",
                price = 129.99,
                quantity = 2,
                imageUrl = "https://example.com/speaker.jpg"
            )

        )
    }
}