package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    onBack: () -> Unit
) {
    var toBuyItems by remember { mutableStateOf(mutableListOf<GroceryItem>()) }
    var inventoryItems by remember { mutableStateOf(mutableListOf<GroceryItem>()) }

    var showAddDialog by remember { mutableStateOf(false) }
    var addingToSection by remember { mutableStateOf(GrocerySection.TO_BUY) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text ="Grocery",
                        fontWeight = FontWeight.Bold
                    )
                        },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }

            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // TO BUY
            GrocerySectionCard(
                title = "To Buy",
                items = toBuyItems,
                onAddClick = {
                    addingToSection = GrocerySection.TO_BUY
                    showAddDialog = true
                },
                onQuantityChange = { item, newQty ->
                    val index = toBuyItems.indexOf(item)
                    if (index >= 0) {
                        toBuyItems[index] = item.copy(quantity = newQty)
                        toBuyItems = toBuyItems.toMutableList()
                    }
                },
                onRemove = { item ->
                    toBuyItems.remove(item)
                    toBuyItems = toBuyItems.toMutableList()
                }
            )

            Spacer(Modifier.height(24.dp))

            // INVENTORY
            GrocerySectionCard(
                title = "Inventory",
                items = inventoryItems,
                onAddClick = {
                    addingToSection = GrocerySection.INVENTORY
                    showAddDialog = true
                },
                onQuantityChange = { item, newQty ->
                    val index = inventoryItems.indexOf(item)
                    if (index >= 0) {
                        inventoryItems[index] = item.copy(quantity = newQty)
                        inventoryItems = inventoryItems.toMutableList()
                    }
                },
                onRemove = { item ->
                    inventoryItems.remove(item)
                    inventoryItems = inventoryItems.toMutableList()
                }
            )
        }
    }

    // Add item popup
    if (showAddDialog) {
        AddItemDialog(
            onCancel = { showAddDialog = false },
            onConfirm = { name, qty ->
                val newItem = GroceryItem(name, qty)
                if (addingToSection == GrocerySection.TO_BUY) {
                    toBuyItems.add(newItem)
                    toBuyItems = toBuyItems.toMutableList()
                } else {
                    inventoryItems.add(newItem)
                    inventoryItems = inventoryItems.toMutableList()
                }
                showAddDialog = false
            }
        )
    }
}


// Data models

data class GroceryItem(
    val name: String,
    val quantity: Int
)

enum class GrocerySection { TO_BUY, INVENTORY }


// Reusable UI components

@Composable
fun GrocerySectionCard(
    title: String,
    items: List<GroceryItem>,
    onAddClick: () -> Unit,
    onQuantityChange: (GroceryItem, Int) -> Unit,
    onRemove: (GroceryItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Button(onClick = onAddClick) { Text("Add") }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                items(items) { item ->
                    GroceryItemRow(
                        item = item,
                        onQuantityChange = { newQty -> onQuantityChange(item, newQty) },
                        onDelete = { onRemove(item) }
                    )
                }
            }
        }
    }
}


@Composable
fun GroceryItemRow(
    item: GroceryItem,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var qty by remember { mutableStateOf(item.quantity.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.name, style = MaterialTheme.typography.bodyLarge)

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = qty,
                onValueChange = {
                    qty = it
                    it.toIntOrNull()?.let { q -> onQuantityChange(q) }
                },
                modifier = Modifier.width(70.dp),
                singleLine = true
            )

            Spacer(Modifier.width(12.dp))

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}


@Composable
fun AddItemDialog(
    onCancel: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Add Item") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") }
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val qty = quantity.toIntOrNull() ?: 1
                onConfirm(name, qty)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}
