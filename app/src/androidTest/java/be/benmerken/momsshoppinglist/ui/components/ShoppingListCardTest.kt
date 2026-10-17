package be.benmerken.momsshoppinglist.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.benmerken.momsshoppinglist.data.model.ShoppingList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingListCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shoppingListCard_displaysListNameAndDescription() {
        val list = ShoppingList("1", "Mom's Shopping List", "Groceries and household items")

        composeTestRule.setContent {
            ShoppingListCard(
                onShoppingListClick = {},
                list = list
            )
        }

        composeTestRule.onNodeWithText("Mom's Shopping List").assertExists()
        composeTestRule.onNodeWithText("Groceries and household items").assertExists()
    }

    @Test
    fun shoppingListCard_runsOnShoppingListClick() {
        val list = ShoppingList("1", "Mom's Shopping List", "Groceries and household items")
        var clickedList: ShoppingList? = null

        composeTestRule.setContent {
            ShoppingListCard(
                onShoppingListClick = { clickedList = it },
                list = list
            )
        }
        composeTestRule.onNodeWithText("Mom's Shopping List").performClick()
        assert(clickedList == list)
    }
}