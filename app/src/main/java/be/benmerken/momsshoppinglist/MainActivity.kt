package be.benmerken.momsshoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import be.benmerken.momsshoppinglist.data.model.ShoppingList
import be.benmerken.momsshoppinglist.ui.shoppinglist.ShoppingListScreen
import be.benmerken.momsshoppinglist.ui.shoppinglist.ShoppingListsScreen
import be.benmerken.momsshoppinglist.ui.theme.MomsShoppingListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomsShoppingListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedShoppingList by remember { mutableStateOf<ShoppingList?>(null) }

                    if (selectedShoppingList == null) {
                        ShoppingListsScreen(
                            onShoppingListClick = { list ->
                                selectedShoppingList = list
                            }
                        )
                    } else {
                        ShoppingListScreen(
                            shoppingList = selectedShoppingList!!,
                            onBackClick = {
                                selectedShoppingList = null
                            }
                        )
                    }
                }
            }
        }
    }
}
