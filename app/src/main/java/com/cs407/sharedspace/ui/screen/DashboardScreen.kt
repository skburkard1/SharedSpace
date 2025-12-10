package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.sharedspace.R
import com.cs407.sharedspace.data.GroupChoreViewModel
import com.cs407.sharedspace.data.UserViewModel
import com.cs407.sharedspace.ui.theme.BgGray
import com.cs407.sharedspace.ui.theme.PurpleGradientTop
import com.cs407.sharedspace.ui.theme.PurplePrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


data class DashboardItem(
    val title: String,
    val icon: Int,
    val route: String,
    val color: Color = Color.White
)

data class ChatItem(
    val name: String,
    val avatar: Int,
    val route: String
)

@Composable
fun DashboardScreen(
    viewModel: UserViewModel,
    choreViewModel: GroupChoreViewModel = viewModel(),
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val chatItems = listOf(
        ChatItem("Group", R.drawable.ic_group, "group_chat"),
        ChatItem("Name 1", R.drawable.ic_user, "chat_name1"),
        ChatItem("Name 2", R.drawable.ic_user, "chat_name2"),
        ChatItem("Name 3", R.drawable.ic_user, "chat_name3")
    )

    val dashboardItems = listOf(
        DashboardItem("My Group", R.drawable.ic_group, "myGroup"),
        DashboardItem("Grocery", R.drawable.ic_grocery, "grocery"),
        DashboardItem("Bill", R.drawable.ic_bill, "bill"),
        DashboardItem("Chore", R.drawable.ic_chore, "chore"),
        DashboardItem("Map", R.drawable.ic_map, "map"),
    )

    var showSignOutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.loadUserData()
    }

    val name = viewModel.userName
    val currentGroupId = viewModel.currentGroupId
    val currentUserId = Firebase.auth.currentUser?.uid

    // get all chores within group
    LaunchedEffect(currentGroupId) {
        if (currentGroupId != null) {
            choreViewModel.listenToGroupChores(currentGroupId)
        }
    }
    // filter to get chores unique to user
    val allChores by choreViewModel.chores.collectAsState()
    val myChores = remember(allChores, currentUserId) {
        allChores.filter {
            // Show only UNDONE chores assigned to user
            it.assignedToId == currentUserId && !it.isDone
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(32.dp)) // put app name closer in line with other screens

        // --- TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Spacer(modifier = Modifier.width(30.dp)) // for correct spacing
            // App title
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { showSignOutDialog = true }) {
                Icon(Icons.AutoMirrored.Outlined.Logout, "Sign out")
            }

        }

        // --- OVERVIEW WIDGET ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hello $name !",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (myChores.isNotEmpty()) {
                        Text(
                            text = "${myChores.size} Pending",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chore List
                if (myChores.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.no_task_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.chore_overview_list),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    LazyColumn {
                        items(myChores) { chore ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Quick Complete Checkbox
                                Checkbox(
                                    checked = false,
                                    onCheckedChange = {
                                        // Mark as done in Firestore
                                        if (currentGroupId != null) {
                                            choreViewModel.toggleChoreStatus(
                                                currentGroupId,
                                                chore.id,
                                                chore.isDone
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = chore.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- CHAT ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Fill remaining space
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PurpleGradientTop,
                            PurplePrimary
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)) {
                Text(
                    text = stringResource(id = R.string.chat_widget_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    chatItems.forEach { chat ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { onNavigate(chat.route) }
                        ) {
                            Image(
                                painter = painterResource(id = chat.avatar),
                                contentDescription = chat.name,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFFEDEDED))
                                    .padding(10.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = chat.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- APPS ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Fill the rest of the PurpleLight column
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(BgGray)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.apps_widget_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 Columns
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f) // Takes up remaining screen space
                ) {
                    items(dashboardItems) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                            modifier = Modifier
                                .height(140.dp)
                                .clickable {
                                    if (item.title == "Grocery" || item.title == "Chore" || item.title == "Map") {
                                        if (currentGroupId != null) {
                                            val baseRoute = when (item.title) {
                                                "Grocery" -> "grocery"
                                                "Chore" -> "chore"
                                                "Map" -> "map"
                                                else -> item.route
                                            }
                                            onNavigate("$baseRoute/$currentGroupId")
                                        } else {
                                            onNavigate("myGroup")
                                        }
                                    } else {
                                        onNavigate(item.route)
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(text = stringResource(id = R.string.sign_out_label)) },
            text = { Text(text = stringResource(id = R.string.sign_out_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text(text = stringResource(id = R.string.sign_out_label))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}