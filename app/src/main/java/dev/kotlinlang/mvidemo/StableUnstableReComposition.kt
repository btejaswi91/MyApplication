package dev.kotlinlang.mvidemo

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Immutable data class (stable)

data class UserData(
    val name: String,
    val age: Int
)

@Composable
fun ImmutableUserDemo() {
    var user by remember { mutableStateOf(UserData("Alice", 25)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Immutable Example (Stable)", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        ImmutableUserCard(
            name = user.name,
            age = user.age,
            onAgeChange = { newAge -> user = user.copy(age = newAge) }
        )
    }
}

@Composable
fun ImmutableUserCard(
    name: String,
    age: Int,
    onAgeChange: (Int) -> Unit
) {
    Column {
        // Log recompositions for the entire card
        Log.d("LOG_COMPOSE", "ImmutableUserCard is recomposing")

        ImmutableNameDisplay(name)
        ImmutableAgeDisplay(age, onAgeChange)
    }
}

@Composable
fun ImmutableNameDisplay(name: String) {
    // Log recompositions for name
    Log.d("LOG_COMPOSE", "ImmutableNameDisplay is recomposing with name: $name")

    Text("Name: $name")
}

@Composable
fun ImmutableAgeDisplay(age: Int, onAgeChange: (Int) -> Unit) {
    // Log recompositions for age
    Log.d("LOG_COMPOSE", "ImmutableAgeDisplay is recomposing with age: $age")

    Column {
        Text("Age: $age")

        Button(onClick = {
            onAgeChange(age + 1)
            Log.d("LOG_COMPOSE", "Age increment requested")
        }) {
            Text("Happy Birthday!")
        }
    }
}