package com.cpx.habitaway.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cpx.habitaway.screens.*

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Profile : Screen("profile")
    object Habits : Screen("habits")
    object StudyRoom : Screen("study_room")
    object Settings : Screen("settings")
    object CreateHabit : Screen("create_habit")
    object Account : Screen("account")
}

@Composable
fun NavGraph(
    navController: NavHostController? = null
) {

    val navControllerFinal = navController ?: rememberNavController()

    NavHost(
        navController = navControllerFinal,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) { MainScreen(navControllerFinal) }
        composable(Screen.Profile.route) { ProfileScreen(navControllerFinal) }
        composable(Screen.Habits.route) { HabitsScreen(navControllerFinal) }
        composable(Screen.StudyRoom.route) { StudyRoomScreen(navControllerFinal) }
        composable(Screen.Settings.route) { SettingsScreen(navControllerFinal) }
        composable(Screen.CreateHabit.route) { CreateHabitScreen(navControllerFinal) }
        composable(Screen.Account.route) { AccountScreen(navControllerFinal) }
        composable("challenge60") { Challenge60( navControllerFinal) }

    }
}



