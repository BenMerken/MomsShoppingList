package be.benmerken.momsshoppinglist.ui.shoppinglist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.benmerken.momsshoppinglist.data.model.ShoppingList
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
            ShoppingListScreen(shoppingList = ShoppingList("1", "Mom's Shopping List"))
        }

        // Verify title
        composeTestRule.onNodeWithText("Mom's Shopping List").assertExists()

        // Verify all dummy items exist
        composeTestRule.onNodeWithText("Milk").assertExists()
        composeTestRule.onNodeWithText("Bread").assertExists()
        composeTestRule.onNodeWithText("Eggs").assertExists()
        composeTestRule.onNodeWithText("Butter").assertExists()
        composeTestRule.onNodeWithText("Apples").assertExists()
        composeTestRule.onNodeWithText("Coffee").assertExists()
    }

    @Test
    fun shoppingListScreen_initialRemainingCountIsCorrect() {
        composeTestRule.setContent {
            ShoppingListScreen(shoppingList = ShoppingList("1", "Mom's Shopping List"))
        }

        // Initial state has 4 unchecked items (Milk, Bread, Butter, Apples)
        composeTestRule.onNodeWithText("Items (4 remaining)").assertExists()
    }

    @Test
    fun shoppingListScreen_checkingItemUpdatesState() {
        composeTestRule.setContent {
            ShoppingListScreen(shoppingList = ShoppingList("1", "Mom's Shopping List"))
        }

        // Initial remaining count is 4
        composeTestRule.onNodeWithText("Items (4 remaining)").assertExists()

        // Click on Milk to check it
        composeTestRule.onNodeWithText("Milk").performClick()

        // Verify remaining count updates to 3
        composeTestRule.onNodeWithText("Items (3 remaining)").assertExists()
    }

    @Test
    fun shoppingListScreen_uncheckingItemIncreasesRemainingCount() {
        composeTestRule.setContent {
            ShoppingListScreen(shoppingList = ShoppingList("1", "Mom's Shopping List"))
        }

        // Click on Eggs (which is initially checked) to uncheck it
        composeTestRule.onNodeWithText("Eggs").performClick()

        // Verify remaining count increases from 4 to 5
        composeTestRule.onNodeWithText("Items (5 remaining)").assertExists()
    }

    @Test
    fun shoppingListScreen_checkingMultipleItemsUpdatesRemainingCount() {
        composeTestRule.setContent {
            ShoppingListScreen(shoppingList = ShoppingList("1", "Mom's Shopping List"))
        }

        // Initial remaining count is 4
        composeTestRule.onNodeWithText("Items (4 remaining)").assertExists()

        // Check Milk
        composeTestRule.onNodeWithText("Milk").performClick()
        composeTestRule.onNodeWithText("Items (3 remaining)").assertExists()

        // Check Bread
        composeTestRule.onNodeWithText("Bread").performClick()
        composeTestRule.onNodeWithText("Items (2 remaining)").assertExists()
    }
}
