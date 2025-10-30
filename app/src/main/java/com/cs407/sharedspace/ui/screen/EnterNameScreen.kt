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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.R

@Composable
fun EnterNameScreen(
    onEnterName: () -> Unit
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
            val name = rememberTextFieldState()

            Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineLarge)
            Column {
                Text(text = stringResource(id = R.string.welcome), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.enter_name_label), style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Name input
                    TextField(
                        state = name,
                        label = { Text(stringResource(id = R.string.name_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )



                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            //TODO: save the name to the account
                            onEnterName()
                        },
                    ) {
                        Text(stringResource(id = R.string.next_button))
                    }


                }

            }

            Spacer(Modifier.height(4.dp))

        }
    }
}