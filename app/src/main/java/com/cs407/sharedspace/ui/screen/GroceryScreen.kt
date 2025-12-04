package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.data.GroceryItemDoc
import com.cs407.sharedspace.data.GroupGroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    groupId: String,
    viewModel: GroupGroceryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    // Begin listening when the screen is shown
    LaunchedEffect(groupId) {
        if (groupId.isNotBlank()) {
            viewModel.listenToGroupGrocery(groupId)
        }
    }

    // Stop Firestore listener when leaving screen
    DisposableEffect(Unit) {
        onDispose { viewModel.stopListening() }
    }

    val allItems by viewModel.items.collectAsState()
    val toBuy = remember(allItems) { allItems.filter { it.section == "toBuy" } }
    val inventory = remember(allItems) { allItems.filter { it.section == "inventory" } }

    var showAddDialog by remember { mutableStateOf(false) }
    var addingSection by remember { mutableStateOf("toBuy") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Grocery",
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
            // "To Buy" Section
            SectionCard(
                modifier = Modifier.weight(1f), // Takes top half of screen
                title = "To Buy",
                items = toBuy,
                onAdd = { addingSection = "toBuy"; showAddDialog = true },
                onQtyChange = { item, newQty ->
                    viewModel.updateItem(groupId, item.id, qty = newQty.toLong())
                },
                onDelete = { item -> viewModel.deleteItem(groupId, item.id) }
            )

            Spacer(Modifier.height(16.dp))

            // "Inventory" Section
            SectionCard(
                modifier = Modifier.weight(1f), // Takes bottom half of screen
                title = "Inventory",
                items = inventory,
                onAdd = { addingSection = "inventory"; showAddDialog = true },
                onQtyChange = { item, newQty ->
                    viewModel.updateItem(groupId, item.id, qty = newQty.toLong())
                },
                onDelete = { item -> viewModel.deleteItem(groupId, item.id) }
            )
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            onCancel = { showAddDialog = false },
            onConfirm = { name, qty ->
                viewModel.addItem(groupId, name, qty.toLong(), addingSection)
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

// Reusable UI components
@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    title: String,
    items: List<GroceryItemDoc>,
    onAdd: () -> Unit,
    onQtyChange: (GroceryItemDoc, Int) -> Unit,
    onDelete: (GroceryItemDoc) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)), // Dashboard Grey
        shape = RoundedCornerShape(16.dp), // Dashboard Rounded Corners
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onAdd) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // List Content
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items) { doc ->
                        GroceryItemRow(
                            // Map the Doc to the UI model
                            item = GroceryItem(name = doc.name, quantity = doc.quantity.toInt()),
                            onQuantityChange = { newQty -> onQtyChange(doc, newQty) },
                            onDelete = { onDelete(doc) }
                        )
                    }
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
    var qty by remember(item.quantity) { mutableStateOf(item.quantity.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = qty,
                onValueChange = {
                    qty = it
                    it.toIntOrNull()?.let { q -> onQuantityChange(q) }
                },
                modifier = Modifier.width(60.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray
                )
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: 1
                    if (name.isNotBlank()) onConfirm(name, qty)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}