package be.benmerken.momsshoppinglist.ui.shoppinglist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shoppingListScreen_displaysTitleAndDummyItems() {
        composeTestRule.setContent {
            ShoppingListScreen()
        }

        // Verify title
        composeTestRule.onNodeWithText("Mom's Shopping List").assertExists()

        // Verify some dummy items exist
        composeTestRule.onNodeWithText("Milk").assertExists()
        composeTestRule.onNodeWithText("Bread").assertExists()
        composeTestRule.onNodeWithText("Eggs").assertExists()
    }

    @Test
    fun shoppingListScreen_checkingItemUpdatesState() {
        composeTestRule.setContent {
            ShoppingListScreen()
        }

        // Click on Milk to check it
        composeTestRule.onNodeWithText("Milk").performClick()

        // Verify remaining count updates
        composeTestRule.onNodeWithText("Items (3 remaining)").assertExists()
    }
}
