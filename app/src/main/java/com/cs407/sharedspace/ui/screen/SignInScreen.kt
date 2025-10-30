package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun SignInScreen(
    onNavigateToDashboard: () -> Unit
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
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            // Controls visibility of the hint banner
            var showHintBanner by remember { mutableStateOf(false) }

            val context = LocalContext.current

            // Auto-hide banner after 3 seconds when shown
            if (showHintBanner) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showHintBanner = false
                }
            }
            Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineLarge)
            Column {
                Text(text = stringResource(id = R.string.sign_in_label), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Username input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(id = R.string.username_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(id = R.string.password_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {  },
                    ) {
                        Text(stringResource(id = R.string.sign_in_label))
                    }

                    Text(text = "or", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)

                    Button(
                        onClick = {  },
                    ) {
                        Text(stringResource(id = R.string.register_label))
                    }
                }

            }

            Spacer(Modifier.height(4.dp))

        }
    }
}