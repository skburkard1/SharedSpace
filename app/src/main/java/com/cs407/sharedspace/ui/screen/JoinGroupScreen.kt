package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.R

@Composable
fun JoinGroupScreen(
    onJoinGroup: () -> Unit,
    onCreateGroup: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Main card UI
            // State variables for user inputs
            val groupcode = rememberTextFieldState()
            val groupname = rememberTextFieldState()

            // Controls visibility of the hint banner
            var showHintBanner by remember { mutableStateOf(false) }

            //val context = LocalContext.current

            // Auto-hide banner after 3 seconds when shown
            if (showHintBanner) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showHintBanner = false
                }
            }
            Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineLarge)
            Column {
                Text(text = stringResource(id = R.string.join_group_label), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Name input
                    TextField(
                        state = groupcode,
                        label = { Text(stringResource(id = R.string.group_id_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(8.dp))


                    Button(
                        onClick = {
                            //TODO: Join the group if it exists, otherwise show error
                            //TODO: may also want to add an additional screen verifying that the group exists
                            onJoinGroup()
                        },
                    ) {
                        Text(stringResource(id = R.string.join_group_label))
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(text = "or", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.height(8.dp))


                }

                Text(text = stringResource(id = R.string.create_group_label), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Name input
                    TextField(
                        state = groupname,
                        label = { Text(stringResource(id = R.string.group_name_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(8.dp))


                    Button(
                        onClick = {
                            //TODO: Create the group
                            onCreateGroup()
                        },
                    ) {
                        Text(stringResource(id = R.string.create_group_label))
                    }
                }

            }

            Spacer(Modifier.height(4.dp))

        }
    }
}