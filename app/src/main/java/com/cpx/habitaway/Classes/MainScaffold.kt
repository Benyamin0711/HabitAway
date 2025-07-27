package com.cpx.habitaway.Classes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cpx.habitaway.screens.AuthScreen
import com.cpx.habitaway.screens.MainScreen
import com.cpx.habitaway.screens.ProfileScreen

@Composable
fun MainScaffold() {
    val navController = rememberNavController()

    Scaffold(
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                MainScreen(navController)  // Home = MainScreen
            }
            composable("Auth") {
                AuthScreen(navController)
            }
            composable("profile") {
                ProfileScreen()
            }
        }
    }
}

