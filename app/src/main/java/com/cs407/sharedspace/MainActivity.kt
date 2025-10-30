package com.cs407.sharedspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.sharedspace.ui.screen.SignInScreen
import com.cs407.sharedspace.ui.theme.SharedSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedSpaceTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, // Controller that handles navigation
        startDestination = "sign_in" // First screen to display when app starts
    ) {
        composable("sign_in") {
            SignInScreen(
                onNavigateToDashboard = { navController.navigate("dashboard") }
            )
        }
        composable("join_group") {

        }
        composable("dashboard") {

        }
    }
}

