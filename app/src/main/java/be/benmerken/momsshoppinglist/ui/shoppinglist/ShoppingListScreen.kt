package be.benmerken.momsshoppinglist.ui.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.benmerken.momsshoppinglist.data.model.ShoppingItem
import be.benmerken.momsshoppinglist.ui.components.ShoppingItemRow
import be.benmerken.momsshoppinglist.ui.theme.MomsShoppingListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier
) {
    var items by remember {
        mutableStateOf(
            listOf(
                ShoppingItem("1", "Milk", false),
                ShoppingItem("2", "Bread", false),
                ShoppingItem("3", "Eggs", true),
                ShoppingItem("4", "Butter", false),
                ShoppingItem("5", "Apples", false),
                ShoppingItem("6", "Coffee", true)
            )
        )
    }

    var draggingIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mom's Shopping List",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Items (${items.count { !it.isChecked }} remaining)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item -> item.id }
                ) { index, item ->
                    var dragOffsetY by remember { mutableStateOf(0f) }

                    ShoppingItemRow(
                        item = item,
                        onCheckedChange = { checked ->
                            items = items.map {
                                if (it.id == item.id) it.copy(isChecked = checked) else it
                            }
                        },
                        modifier = Modifier.background(
                            if (draggingIndex == index) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        dragHandleModifier = Modifier.pointerInput(index) {
                            detectDragGestures(
                                onDragStart = {
                                    draggingIndex = index
                                    dragOffsetY = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffsetY += dragAmount.y
                                    val itemHeightPx = 100f // Threshold for reordering
                                    if (dragOffsetY > itemHeightPx && index < items.size - 1) {
                                        // Move down
                                        val mutableList = items.toMutableList()
                                        val movedItem = mutableList.removeAt(index)
                                        mutableList.add(index + 1, movedItem)
                                        items = mutableList
                                        draggingIndex = index + 1
                                        dragOffsetY = 0f
                                    } else if (dragOffsetY < -itemHeightPx && index > 0) {
                                        // Move up
                                        val mutableList = items.toMutableList()
                                        val movedItem = mutableList.removeAt(index)
                                        mutableList.add(index - 1, movedItem)
                                        items = mutableList
                                        draggingIndex = index - 1
                                        dragOffsetY = 0f
                                    }
                                },
                                onDragEnd = {
                                    draggingIndex = null
                                    dragOffsetY = 0f
                                },
                                onDragCancel = {
                                    draggingIndex = null
                                    dragOffsetY = 0f
                                }
                            )
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview() {
    MomsShoppingListTheme {
        ShoppingListScreen()
    }
}
