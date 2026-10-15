package be.benmerken.momsshoppinglist.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.benmerken.momsshoppinglist.data.model.ShoppingItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingItemRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shoppingItemRow_displaysItemNameAndState() {
        val item = ShoppingItem(id = "1", name = "Apples", isChecked = false)

        composeTestRule.setContent {
            ShoppingItemRow(
                item = item,
                onCheckedChange = {}
            )
        }

        composeTestRule.onNodeWithText("Apples").assertExists()
    }

    @Test
    fun shoppingItemRow_clickTogglesCheckbox() {
        var isChecked = false
        val item = ShoppingItem(id = "1", name = "Milk", isChecked = false)

        composeTestRule.setContent {
            var currentItem by mutableStateOf(item)
            ShoppingItemRow(
                item = currentItem,
                onCheckedChange = { checked ->
                    isChecked = checked
                    currentItem = currentItem.copy(isChecked = checked)
                }
            )
        }

        composeTestRule.onNodeWithText("Milk").performClick()
        assert(isChecked)
    }
}
