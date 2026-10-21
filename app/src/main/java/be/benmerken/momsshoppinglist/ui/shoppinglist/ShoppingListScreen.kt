package be.benmerken.momsshoppinglist.ui.shoppinglist

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.benmerken.momsshoppinglist.R
import be.benmerken.momsshoppinglist.data.model.ShoppingItem
import be.benmerken.momsshoppinglist.data.model.ShoppingList
import be.benmerken.momsshoppinglist.ui.components.ShoppingItemRow
import be.benmerken.momsshoppinglist.ui.theme.MomsShoppingListTheme
import be.benmerken.momsshoppinglist.ui.util.DraggableItem
import be.benmerken.momsshoppinglist.ui.util.rememberDragDropState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    shoppingList: ShoppingList,
    onBackClick: () -> Unit = {},
) {
    var items by remember {
        mutableStateOf(
            listOf(
                ShoppingItem("1", "Milk", false, 0),
                ShoppingItem("2", "Bread", false, 1),
                ShoppingItem("3", "Eggs", true, 2),
                ShoppingItem("4", "Butter", false, 3),
                ShoppingItem("5", "Apples", false, 4),
                ShoppingItem("6", "Coffee", true, 5)
            )
        )
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    val dragDropState = rememberDragDropState(lazyListState = listState) { fromIndex, toIndex ->
        if (fromIndex in items.indices && toIndex in items.indices) {
            val mutableList = items.toMutableList()
            val item = mutableList.removeAt(fromIndex)
            mutableList.add(toIndex, item)
            items = mutableList.mapIndexed { index, shoppingItem ->
                shoppingItem.copy(listOrder = index)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = shoppingList.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
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
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item -> item.id }
                ) { index, item ->
                    val currentItemIndex by rememberUpdatedState(index)

                    DraggableItem(
                        dragDropState = dragDropState,
                        index = currentItemIndex,
                        modifier = Modifier.fillMaxWidth()
                    ) { isDragging ->
                        ShoppingItemRow(
                            item = item,
                            onCheckedChange = { checked ->
                                items = items.map {
                                    if (it.id == item.id) it.copy(isChecked = checked) else it
                                }
                            },
                            dragHandleModifier = Modifier.pointerInput(item.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        dragDropState.onDragStartForIndex(currentItemIndex)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragDropState.onDrag(dragAmount)
                                        if (overscrollJob?.isActive == true) return@detectDragGesturesAfterLongPress
                                        dragDropState
                                            .checkForOverScroll()
                                            .takeIf { it != 0f }
                                            ?.let {
                                                overscrollJob = scope.launch {
                                                    listState.animateScrollBy(
                                                        it * 1.3f,
                                                        tween(easing = FastOutLinearInEasing)
                                                    )
                                                }
                                            } ?: run { overscrollJob?.cancel() }
                                    },
                                    onDragEnd = {
                                        dragDropState.onDragInterrupted()
                                        overscrollJob?.cancel()
                                    },
                                    onDragCancel = {
                                        dragDropState.onDragInterrupted()
                                        overscrollJob?.cancel()
                                    }
                                )
                            },
                            modifier = Modifier.background(
                                if (isDragging) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
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
}

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview() {
    MomsShoppingListTheme {
        ShoppingListScreen(
            shoppingList = ShoppingList(
                "1",
                "Mom's Shopping List",
                description = ""
            )
        )
    }
}
