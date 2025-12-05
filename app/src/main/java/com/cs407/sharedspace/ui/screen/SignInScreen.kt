package com.cs407.sharedspace.ui.screen

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
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs407.sharedspace.R
import com.cs407.sharedspace.data.UserViewModel
import com.cs407.sharedspace.ui.theme.PurpleGradientTop
import com.cs407.sharedspace.ui.theme.PurplePrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun checkEmail(email: String): EmailResult {
    if (email.isEmpty())
        return EmailResult.Empty
    // 1. username of email should only contain "0-9, a-z, _, A-Z, ."
    // 2. there is one and only one "@" between username and server address
    // 3. there are multiple domain names with at least one top-level domain
    // 4. domain name "0-9, a-z, -, A-Z" (could not have "_" but "-" is valid)
    // 5. multiple domain separate with '.'
    // 6. top level domain should only contain letters and at lest 2 letters
    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
    return if (pattern.matches(email)) EmailResult.Valid else EmailResult.Invalid
}

enum class PasswordResult {
    Valid,
    Empty,
    Short,
    Invalid
}

fun checkPassword(password: String): PasswordResult {
    // 1. password should contain at least one uppercase letter, lowercase letter, one digit
    // 2. minimum length: 5
    if (password.isEmpty())
        return PasswordResult.Empty
    if (password.length < 5)
        return PasswordResult.Short
    if (Regex("\\d+").containsMatchIn(password) &&
        Regex("[a-z]+").containsMatchIn(password) &&
        Regex("[A-Z]+").containsMatchIn(password)
    )
        return PasswordResult.Valid
    return PasswordResult.Invalid
}

fun createAccount(
    email: String,
    password: String,
    onComplete: (Boolean, Exception?, FirebaseUser?) -> Unit,
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception, auth.currentUser)
        }
}

fun signIn(
    email: String,
    password: String,
    onComplete: (Boolean, Exception?, FirebaseUser?) -> Unit,
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception, auth.currentUser)

        }
}

@Composable
fun SignInScreen(
    onSignIn: () -> Unit,
    onRegister: () -> Unit,
    viewModel: UserViewModel
) {
    // Main card UI
    // State variables for user inputs
    val email = rememberTextFieldState()
    val password = rememberTextFieldState()
    var error: String? by remember { mutableStateOf(null) }


    //TODO: Adjust signin and register to use DAO and update viewModel
    val onCompleteSignIn: (Boolean, Exception?, FirebaseUser?) -> Unit =
        { isSuccess, taskException, signedUser ->
            if (isSuccess && signedUser != null)
                onSignIn()
            else
                error = taskException?.message
        }

    val onCompleteRegister: (Boolean, Exception?, FirebaseUser?) -> Unit =
        { isSuccess, taskException, signedUser ->
            if (isSuccess && signedUser != null)
                onRegister()
            else
                error = taskException?.message
        }

    //TODO: Uncomment following code once logout is functional
    /*
    val user = Firebase.auth.currentUser
    if (user != null)
        onComplete(true, null, user)
    */

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

            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
            Column {
                Text(
                    text = stringResource(id = R.string.sign_in_label),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ErrorText(error)
                    // Email input
                    TextField(
                        state = email,
                        label = { Text(stringResource(id = R.string.email_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(8.dp))

                    // Password input
                    SecureTextField(
                        state = password,
                        label = { Text(stringResource(id = R.string.password_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        textObfuscationMode = TextObfuscationMode.Hidden,
                    )

                    Spacer(Modifier.height(16.dp))

                    SignInOrRegisterButton(
                        email.text.toString(),
                        password.text.toString(),
                        onCompleteSignIn,
                        true
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "or",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    SignInOrRegisterButton(
                        email.text.toString(),
                        password.text.toString(),
                        onCompleteRegister,
                        false
                    )
                }

            }

            Spacer(Modifier.height(4.dp))

        }
    }
}

@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (error != null)
        Text(text = error, color = Color.Red, textAlign = TextAlign.Center)
}

@Composable
fun SignInOrRegisterButton(
    email: String,
    password: String,
    onComplete: (Boolean, Exception?, FirebaseUser?) -> Unit,
    signIn: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = {
            var errorString: String? = null

            val emailResult = checkEmail(email)
            if (emailResult == EmailResult.Empty) {
                errorString = context.getString(R.string.empty_email)
            } else if (emailResult == EmailResult.Invalid) {
                errorString = context.getString(R.string.invalid_email)
            }

            val passwordResult = checkPassword(password)
            if (errorString == null) {
                errorString = when (passwordResult) {
                    PasswordResult.Empty -> context.getString(R.string.empty_password)
                    PasswordResult.Short -> context.getString(R.string.short_password)
                    PasswordResult.Invalid -> context.getString(R.string.invalid_password)
                    PasswordResult.Valid -> null
                }
            }

            if (errorString != null)
                onComplete(false, Exception(errorString), null)
            else if (signIn)
                signIn(email, password, onComplete)
            else
                createAccount(email, password, onComplete)
        },
        //make default button color transparent so the custom colors show through
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
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
                text = if (signIn) stringResource(R.string.sign_in_label) else stringResource(R.string.register_label),
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
