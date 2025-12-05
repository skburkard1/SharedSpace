package com.cs407.sharedspace.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.sharedspace.R
import kotlinx.coroutines.launch


@Composable
fun BillsScreen(
    onBack: () -> Unit
    // navController: NavController,
    // viewModel: SharedSpaceViewModel
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val displayName = if (name.isBlank()) "  " else name
    val displayAmount = amount.toDoubleOrNull() ?: 0.0

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 40.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            //verticalArrangement = Arrangement.Center,
            //horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp)) // put app name closer in line with other screens
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                    // App title
                    Text(
                        text = "Bill",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(24.dp)) // for correct spacing
                }
        }
            ///
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                //TODO: Find the avatar of the person in backend Data, if didn't find, the avatar remain as default
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.height(24.dp))
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
                .heightIn(min = 420.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7FCFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ){
                Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "To: $displayName   $${"%.2f".format(displayAmount)}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


            Spacer(modifier = Modifier.height(24.dp))

            // Person name
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    successMessage = ""
                },
                label = { Text("To who") },
                singleLine = true,
                modifier = Modifier.width(280.dp)
                    .align(Alignment.CenterHorizontally),

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
                modifier = Modifier.width(280.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                 shape = RoundedCornerShape(30.dp),
                 elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                 modifier = Modifier.width(280.dp)
                     .align(Alignment.CenterHorizontally),
            ) {
            Button(
                modifier = Modifier.width(280.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    val amountValue = amount.toDoubleOrNull()

                    when {
                        name.isBlank() -> {
                            error = "Please enter a name."
                            successMessage = ""
                        }

                        //TODO: if the name id not in the group, show the error message "the people is not in the group"
                        amountValue == null || amountValue <= 0.0 -> {
                            error = "Please enter a valid amount."
                            successMessage = ""
                        }

                        else -> {
                            error = ""
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Successfully sent the bill to $name"

                                )
                            }

                            // This is where you hook into your real logic:
                            // viewModel.addBill(name, amountValue)
                            // or save to database, etc.
                        }
                    }
                }
            ) {
                Text("Confirm")
                }
            }
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
}

