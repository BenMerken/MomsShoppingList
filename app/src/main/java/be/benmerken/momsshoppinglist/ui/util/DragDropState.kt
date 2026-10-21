package be.benmerken.momsshoppinglist.ui.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState,
    onSwap: (Int, Int) -> Unit,
): DragDropState {
    val scope = rememberCoroutineScope()
    return remember(lazyListState) {
        DragDropState(
            state = lazyListState,
            onSwap = onSwap,
            scope = scope,
        )
    }
}

fun LazyListState.getVisibleItemInfoFor(absoluteIndex: Int): LazyListItemInfo? {
    return this.layoutInfo.visibleItemsInfo.firstOrNull { it.index == absoluteIndex }
}

val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size

@Composable
fun LazyItemScope.DraggableItem(
    dragDropState: DragDropState,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(isDragging: Boolean) -> Unit,
) {
    val current: Float by animateFloatAsState(
        targetValue = dragDropState.draggingItemOffset * 0.67f,
        label = "draggingOffset",
    )
    val previous: Float by animateFloatAsState(
        targetValue = dragDropState.previousItemOffset.value * 0.67f,
        label = "previousOffset",
    )
    val dragging = index == dragDropState.currentIndexOfDraggedItem

    val draggingModifier = if (dragging) {
        Modifier
            .zIndex(1f)
            .graphicsLayer { translationY = current }
    } else if (index == dragDropState.previousIndexOfDraggedItem) {
        Modifier
            .zIndex(1f)
            .graphicsLayer { translationY = previous }
    } else {
        Modifier.animateItem(
            fadeInSpec = null,
            fadeOutSpec = null,
            placementSpec = tween(easing = FastOutLinearInEasing),
        )
    }

    Column(modifier = modifier.then(draggingModifier)) {
        content(dragging)
    }
}

class DragDropState internal constructor(
    val state: LazyListState,
    private val scope: CoroutineScope,
    private val onSwap: (Int, Int) -> Unit,
) {
    private var draggedDistance by mutableFloatStateOf(0f)
    private var draggingItemInitialOffset by mutableIntStateOf(0)

    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            (draggingItemInitialOffset + draggedDistance) - item.offset
        } ?: 0f

    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == currentIndexOfDraggedItem }

    internal var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set

    internal var previousItemOffset = Animatable(0f)
        private set

    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)

    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set

    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let { Pair(it.offset, it.offsetEnd) }

    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let { state.getVisibleItemInfoFor(absoluteIndex = it) }

    fun onDragStartForIndex(index: Int) {
        previousIndexOfDraggedItem = null
        scope.launch {
            previousItemOffset.snapTo(0f)
        }
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == index }
            ?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
                draggingItemInitialOffset = it.offset
                draggedDistance = 0f
            }
    }

    fun onDragInterrupted() {
        if (currentIndexOfDraggedItem != null) {
            previousIndexOfDraggedItem = currentIndexOfDraggedItem
            val startOffset = draggingItemOffset
            scope.launch {
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    0f,
                    tween(easing = FastOutLinearInEasing),
                )
                previousIndexOfDraggedItem = null
            }
        }
        draggingItemInitialOffset = 0
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y
        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hovered ->
                val targetItem = if (endOffset > (hovered.offset + (hovered.size / 2))) {
                    state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                        (item.index > hovered.index) && (endOffset > (item.offset + (item.size / 2)))
                    }
                } else if (startOffset < hovered.offset) {
                    state.layoutInfo.visibleItemsInfo.lastOrNull { item ->
                        (item.index < hovered.index) && (startOffset < (item.offset + (item.size / 2)))
                    }
                } else null

                targetItem?.let { item ->
                    currentIndexOfDraggedItem?.let { current ->
                        scope.launch {
                            onSwap(current, item.index)
                        }
                    }
                    currentIndexOfDraggedItem = item.index
                }
            }
        }
    }

    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance

            return@let when {
                draggedDistance > 0 -> ((endOffset - state.layoutInfo.viewportEndOffset) + 50f).takeIf { diff -> diff > 0 }
                draggedDistance < 0 -> ((startOffset - state.layoutInfo.viewportStartOffset) - 50f).takeIf { diff -> diff < 0 }
                else -> null
            }
        } ?: 0f
    }
}
