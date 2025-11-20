package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.sharedspace.R


@Composable
fun BillsScreen(
    // add your parameters back if you have them, e.g.
    // navController: NavController,
    // viewModel: SharedSpaceViewModel
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Bill / Transfer",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Person name
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                successMessage = ""
            },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Amount of money
        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
                successMessage = ""
            },
            label = { Text("Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val amountValue = amount.toDoubleOrNull()

                when {
                    name.isBlank() -> {
                        error = "Please enter a name."
                        successMessage = ""
                    }
                    amountValue == null || amountValue <= 0.0 -> {
                        error = "Please enter a valid amount."
                        successMessage = ""
                    }
                    else -> {
                        error = ""
                        successMessage = "You transferred $amountValue to $name."

                        // 👉 This is where you hook into your real logic:
                        // viewModel.addBill(name, amountValue)
                        // or save to database, etc.
                    }
                }
            }
        ) {
            Text("Save / Transfer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}