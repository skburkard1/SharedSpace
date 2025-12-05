package com.cs407.sharedspace.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.R
import com.cs407.sharedspace.data.GroupViewModel
import com.cs407.sharedspace.ui.theme.PurpleGradientTop
import com.cs407.sharedspace.ui.theme.PurplePrimary

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun JoinGroupScreen(
    onJoinGroup: () -> Unit,
    onCreateGroup: () -> Unit
) {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<GroupViewModel>()
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // State variables for user inputs
            val groupcode = rememberTextFieldState()
            val groupname = rememberTextFieldState()

            // Controls visibility of the hint banner
            var showHintBanner by remember { mutableStateOf(false) }

            // Auto-hide banner after 3 seconds when shown
            if (showHintBanner) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showHintBanner = false
                }
            }

            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )

            Column {
                // --- JOIN SECTION ---
                Text(
                    text = stringResource(id = R.string.join_group_label),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Group Code input
                    TextField(
                        state = groupcode,
                        label = { Text(stringResource(id = R.string.group_id_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val code: String = groupcode.text.toString().trim()

                            if (code.isBlank()) {
                                showToast(context, "Enter a group code")
                                return@Button
                            }

                            viewModel.joinGroup(
                                groupId = code,
                                onSuccess = {
                                    showToast(context, "Group Joined !")
                                    onJoinGroup()
                                },
                                onFailure = {
                                    showToast(context, it.message ?: "Joining Group Failed.")
                                })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(PurpleGradientTop, PurplePrimary)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.join_group_label),
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "or",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.height(12.dp))
                }

                // --- CREATE SECTION ---
                Text(
                    text = stringResource(id = R.string.create_group_label),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Group name input
                    TextField(
                        state = groupname,
                        label = { Text(stringResource(id = R.string.group_name_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val groupName = groupname.text.toString().trim()

                            if (groupName.isBlank()) {
                                showToast(context, "Enter a valid group name")
                                return@Button
                            }
                            viewModel.createGroup(
                                name = groupName,
                                onSuccess = {
                                    showToast(context, "Group created successfully")
                                    onCreateGroup()
                                },
                                onFailure = {
                                    showToast(context, it.message ?: "Failed to create group")
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(PurpleGradientTop, PurplePrimary)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.create_group_label),
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}