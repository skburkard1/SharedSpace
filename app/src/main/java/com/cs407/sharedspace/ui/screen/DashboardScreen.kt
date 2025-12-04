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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.sharedspace.R
import com.cs407.sharedspace.data.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

val PurplePrimary = Color(0xFFD0BCFF)
val PurpleLight = Color(0xFFCCC2DC)
val TextWhite = Color.White
val BgGray = Color(0xFFF5F6FA)

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
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val chatItems = listOf(
        ChatItem("Group", R.drawable.ic_user, "group_chat"),
        ChatItem("Name 1", R.drawable.ic_user, "chat_name1"),
        ChatItem("Name 2", R.drawable.ic_user, "chat_name2"),
        ChatItem("Name 3", R.drawable.ic_user, "chat_name3")
    )

    val dashboardItems = listOf(
        DashboardItem("My Groups", R.drawable.ic_user, "myGroups"),
        DashboardItem("Grocery", R.drawable.ic_grocery, "grocery"),
        DashboardItem("Bill", R.drawable.ic_bill, "bill"),
        DashboardItem("Chore", R.drawable.ic_chore, "chore"),
        DashboardItem("Map", R.drawable.ic_map, "map"),
    )

    LaunchedEffect(true) {
        viewModel.loadUserData()
    }

    val name = viewModel.userName
    val currentGroupId = viewModel.currentGroupId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(32.dp)) // put app name closer in line with other screens
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Spacer(modifier = Modifier.width(24.dp)) // for correct spacing
            // App title
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onSignOut) {
                //TODO: Add additional sign out pop up
                Icon(Icons.AutoMirrored.Outlined.Logout, "Sign out")
            }

        }


        // Overview Widget
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Text(
                    text = "Hello $name",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chat Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Fill remaining space
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(PurplePrimary)
        ) {
            Column(modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)) {
                Text(
                    text = "Chat",
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
                                    .padding(12.dp)
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Fill the rest of the PurpleLight column
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(BgGray)
                    .padding(24.dp)
            ) {
                Text(
                    text = "What would you like to do today?",
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
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)), // Light background
                            modifier = Modifier
                                .height(140.dp)
                                .clickable {
                                    if (item.title == "Grocery") {
                                        if (currentGroupId != null) {
                                            onNavigate("grocery/$currentGroupId")
                                        } else {
                                            onNavigate("myGroups")
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
}