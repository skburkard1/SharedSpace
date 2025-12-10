package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.sharedspace.R
import com.cs407.sharedspace.data.ChoreItem
import com.cs407.sharedspace.data.GroupChoreViewModel
import com.cs407.sharedspace.data.GroupMember

val iconMap = mapOf(
    "Cleaning" to R.drawable.ic_cleaning,
    "Dishes" to R.drawable.ic_dishes,
    "Trash" to R.drawable.ic_trash,
    "Laundry" to R.drawable.ic_laundry,
    "Vacuuming" to R.drawable.ic_vacuum,
    "Watering" to R.drawable.ic_watering,
    "Mowing" to R.drawable.ic_mowing,
    "Cat" to R.drawable.ic_cat,
    "Dog" to R.drawable.ic_dog
)

@Composable
fun ChoreScreen(
    groupId: String,
    viewModel: GroupChoreViewModel = viewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(groupId) {
        viewModel.listenToGroupChores(groupId)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopListening() }
    }

    val chores by viewModel.chores.collectAsState()
    val members by viewModel.members.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Chore")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
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
                    text = stringResource(id = R.string.chore_label),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Chore list
            if (chores.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(id = R.string.no_chore_message))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(chores) { chore ->
                        ChoreCard(
                            chore = chore,
                            onChecked = { viewModel.toggleChoreStatus(groupId, chore.id, chore.isDone) },
                            onDelete = { viewModel.deleteChore(groupId, chore.id) }
                        )
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddChoreDialog(
            members = members,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, member, repeat, type ->
                viewModel.addChore(groupId, name, member, repeat, type)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ChoreCard(
    chore: ChoreItem,
    onChecked: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(chore.isDone) Color(0xFFF0F0F0) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = if (iconMap[chore.type] != null) iconMap[chore.type]!! else R.drawable.ic_chore),
                    contentDescription = "Chore Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 12.dp)
                )

                Column {
                    Text(
                        text = chore.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if(chore.isDone) Color.Gray else Color.Black
                    )
                    Text(
                        text = "Repeats: ${chore.repeat}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Assigned to: ${chore.assignedToName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = chore.isDone,
                    onCheckedChange = { onChecked() }
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AddChoreDialog(
    members: List<GroupMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, GroupMember, String, String) -> Unit
) {
    var expandedType by remember { mutableStateOf(false) }
    val typeOptions = listOf("Cleaning", "Dishes", "Trash", "Laundry", "Vacuuming", "Watering", "Mowing", "Dog", "Cat")
    var selectedType by remember { mutableStateOf(typeOptions[0]) }

    var name by remember { mutableStateOf("") }
    var expandedMember by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }

    var expandedRepeat by remember { mutableStateOf(false) }
    val repeatOptions = listOf("One-time", "Daily", "Twice weekly", "Weekly", "Every two weeks", "Monthly")
    var selectedRepeat by remember { mutableStateOf(repeatOptions[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.add_chore)) },
        text = {
            Column {
                // Icon Dropdown
                Text("Icon:", style = MaterialTheme.typography.bodySmall)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedType = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedType)
                    }
                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        typeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedType = option
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.new_chore_name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // Assignee Dropdown
                Text(text = stringResource(id = R.string.chore_assign_to),
                    style = MaterialTheme.typography.bodySmall)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedMember = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedMember?.name ?: "Select Member")
                    }
                    DropdownMenu(
                        expanded = expandedMember,
                        onDismissRequest = { expandedMember = false }
                    ) {
                        members.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member.name) },
                                onClick = {
                                    selectedMember = member
                                    expandedMember = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Repeat Dropdown
                Text(text = stringResource(id = R.string.chore_repeat),
                    style = MaterialTheme.typography.bodySmall)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedRepeat = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedRepeat)
                    }
                    DropdownMenu(
                        expanded = expandedRepeat,
                        onDismissRequest = { expandedRepeat = false }
                    ) {
                        repeatOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedRepeat = option
                                    expandedRepeat = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && selectedMember != null) {
                        onConfirm(name, selectedMember!!, selectedRepeat, selectedType)
                    }
                },
                enabled = name.isNotEmpty() && selectedMember != null
            ) {
                Text(text = stringResource(id = R.string.add))            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.cancel)) }
        }
    )
}