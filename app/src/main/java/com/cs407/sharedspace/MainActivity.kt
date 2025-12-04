package com.cs407.sharedspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.sharedspace.data.GroupListViewModel
import com.cs407.sharedspace.data.UserViewModel
import com.cs407.sharedspace.ui.screen.BillsScreen
import com.cs407.sharedspace.ui.screen.ChoreScreen
import com.cs407.sharedspace.ui.screen.DashboardScreen
import com.cs407.sharedspace.ui.screen.EnterNameScreen
import com.cs407.sharedspace.ui.screen.GroceryScreen
import com.cs407.sharedspace.ui.screen.JoinGroupScreen
import com.cs407.sharedspace.ui.screen.MyGroupsScreen
import com.cs407.sharedspace.ui.screen.SignInScreen
import com.cs407.sharedspace.ui.theme.SharedSpaceTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
    val viewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController, // Controller that handles navigation
        startDestination = "sign_in" // First screen to display when app starts
    ) {
        composable("sign_in") {
            SignInScreen(
                onSignIn = { navController.navigate("dashboard") },
                onRegister = { navController.navigate("enter_name") },
                viewModel = viewModel
            )
        }
        composable("join_group") {
            JoinGroupScreen(
                onJoinGroup = { navController.navigate("dashboard") },
                onCreateGroup = { navController.navigate("dashboard") }
            )
        }
        composable("enter_name") {
            EnterNameScreen(
                onEnterName = { navController.navigate("join_group") },
                viewModel = viewModel
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onNavigate = { route -> navController.navigate(route) },
                onSignOut = { Firebase.auth.signOut(); navController.navigate("sign_in") },
                viewModel = viewModel
            )
        }
        composable("grocery/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
            GroceryScreen(groupId = groupId, onBack = { navController.navigate("dashboard") })
        }

        composable("chore") {
            ChoreScreen(
                onBack = { navController.navigate("dashboard") }
            )
        }
        composable("bill") {
            BillsScreen()
        }
        composable("myGroups") {
            val userViewModel: UserViewModel = viewModel()
            val groupListViewModel: GroupListViewModel = viewModel()


            MyGroupsScreen(
                viewModel = userViewModel,
                groupListViewModel = groupListViewModel,
                onGroupSelected = { groupId ->
                    navController.navigate("group_detail/$groupId")
                },
                onBack = { navController.navigate("dashboard") }
            )
        }
    }
}

