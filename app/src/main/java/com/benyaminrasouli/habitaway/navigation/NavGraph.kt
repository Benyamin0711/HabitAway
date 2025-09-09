package com.benyaminrasouli.habitaway.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.benyaminrasouli.habitaway.screens.*
import com.benyaminrasouli.habitaway.viewmodel.UserViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")
    object Profile : Screen("profile")
    object Habits : Screen("habits")
    object StudyRoom : Screen("study_room")
    object Settings : Screen("settings")
    object CreateHabit : Screen("create_habit")
    object Account : Screen("account")
}

@Composable
fun NavGraph(navController: NavHostController? = null) {
    val nav = navController ?: rememberNavController()
    val context = LocalContext.current
    val userVM: UserViewModel = viewModel(
        factory = UserViewModel.Factory(context.applicationContext as Application)
    )

    val isLoggedInState = userVM.isLoggedIn.collectAsState(initial = null) // ← اول null

    NavHost(
        navController = nav,
        startDestination = Screen.Splash.route // همیشه اول اسپلش
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    val dest = if (isLoggedInState.value == true) {
                        Screen.Main.route
                    } else {
                        Screen.Account.route
                    }
                    nav.navigate(dest) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Account.route) {
            AuthScreen(navController = nav, userViewModel = userVM)
        }
        composable(Screen.Main.route) { MainScreen(nav) }
        composable(Screen.Profile.route) { ProfileScreen(nav) }
        composable(Screen.Habits.route) { HabitsScreen(nav) }
        composable(Screen.StudyRoom.route) { StudyRoomScreen(nav) }
        composable(Screen.Settings.route) { SettingsScreen(nav) }
        composable(Screen.CreateHabit.route) { CreateHabitScreen(nav) }
        composable("challenge60") { Challenge60(nav) }
    }
}

