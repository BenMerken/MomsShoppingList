package be.benmerken.momsshoppinglist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.benmerken.momsshoppinglist.data.model.ShoppingItem
import be.benmerken.momsshoppinglist.ui.theme.MomsShoppingListTheme

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!item.isChecked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
            color = if (item.isChecked) {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingItemRowPreview() {
    MomsShoppingListTheme {
        ShoppingItemRow(
            item = ShoppingItem(id = "1", name = "Whole Milk", isChecked = false),
            onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingItemRowCheckedPreview() {
    MomsShoppingListTheme {
        ShoppingItemRow(
            item = ShoppingItem(id = "2", name = "Whole Wheat Bread", isChecked = true),
            onCheckedChange = {}
        )
    }
}
