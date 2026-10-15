package be.benmerken.momsshoppinglist.data.model

data class ShoppingList(
    val id: String,
    val name: String,
    val description: String = ""
)
