import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ItemWithoutKeys(item: String) {
    var count by remember { mutableStateOf(0) }
    Log.d("Recomposition", "Composing item: $item with count $count")
    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = "$item: $count", modifier = Modifier.weight(1f))
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}

@Composable
fun ItemsListWithoutKeys(items: List<String>) {
    Column {
        items.forEach { item ->
            ItemWithoutKeys(item)
        }
    }
}

@Composable
fun ItemsListWithKeys(items: List<String>) {
    Column {
        items.forEach { item ->
            key(item) {
                ItemWithoutKeys(item)
            }
        }
    }
}



