package com.cs407.sharedspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.sharedspace.ui.screen.DashboardScreen
import com.cs407.sharedspace.ui.screen.EnterNameScreen
import com.cs407.sharedspace.ui.screen.JoinGroupScreen
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
                onSignIn = { navController.navigate("dashboard")},
                onRegister = { navController.navigate("enter_name")}
            )
        }
        composable("join_group") {
            JoinGroupScreen(
                onJoinGroup = { navController.navigate("dashboard")},
                onCreateGroup = { navController.navigate("dashboard")}
            )
        }
        composable("enter_name") {
            EnterNameScreen(
                onEnterName = { navController.navigate("join_group")}
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}

