package be.benmerken.momsshoppinglist.ui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.benmerken.momsshoppinglist.data.model.ShoppingList
import be.benmerken.momsshoppinglist.ui.components.ShoppingListCard
import be.benmerken.momsshoppinglist.ui.theme.MomsShoppingListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsScreen(
    modifier: Modifier = Modifier,
    onShoppingListClick: (ShoppingList) -> Unit = {}
) {
    val shoppingLists = listOf(
        ShoppingList("1", "Mom's Shopping List", "Groceries and household items"),
        ShoppingList("2", "Weekly Groceries", "Supermarket essentials"),
        ShoppingList("3", "Party Supplies", "Birthdays and celebrations"),
        ShoppingList("4", "Hardware Store", "Tools and DIY supplies")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Shopping Lists",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(shoppingLists) { list ->
                ShoppingListCard(
                    onShoppingListClick = onShoppingListClick,
                    list = list
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListsScreenPreview() {
    MomsShoppingListTheme {
        ShoppingListsScreen()
    }
}
