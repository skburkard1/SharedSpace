package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.sharedspace.R
import com.cs407.sharedspace.data.GroceryItemDoc
import com.cs407.sharedspace.data.GroupGroceryViewModel
import com.cs407.sharedspace.ui.theme.PurpleGradientTop
import com.cs407.sharedspace.ui.theme.PurplePrimary

val CardBackground = Color(0xFFF8F8F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    groupId: String,
    viewModel: GroupGroceryViewModel = viewModel(),
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


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
            }
            Text(
                text = stringResource(id = R.string.grocery_label),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // ---TO BUY---
        SectionCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.grocery_toBuy_label),
            items = toBuy,
            onAdd = { addingSection = "toBuy"; showAddDialog = true },
            onQtyChange = { item, newQty ->
                viewModel.updateItem(groupId, item.id, qty = newQty.toLong())
            },
            onDelete = { item -> viewModel.deleteItem(groupId, item.id) }
        )

        Spacer(Modifier.height(16.dp))

        // ---INVENTORY---
        SectionCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.grocery_inventory_label),
            items = inventory,
            onAdd = { addingSection = "inventory"; showAddDialog = true },
            onQtyChange = { item, newQty ->
                viewModel.updateItem(groupId, item.id, qty = newQty.toLong())
            },
            onDelete = { item -> viewModel.deleteItem(groupId, item.id) }
        )
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

// Data model for UI
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
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
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

                // Add button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(PurpleGradientTop, PurplePrimary)
                            )
                        )
                        .clickable { onAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Item",
                        tint = Color.DarkGray
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // List Content
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items) { doc ->
                        GroceryItemRow(
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
            // Small Quantity Field
            OutlinedTextField(
                value = qty,
                onValueChange = {
                    qty = it
                    it.toIntOrNull()?.let { q -> onQuantityChange(q) }
                },
                modifier = Modifier
                    .width(60.dp)
                    .height(50.dp),
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
        title = { Text(text = stringResource(id = R.string.add_grocery)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.new_grocery_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(text = stringResource(id = R.string.grocery_quantity)) },
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
                Text(text = stringResource(id = R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(text = stringResource(id = R.string.cancel)) }
        }
    )
}