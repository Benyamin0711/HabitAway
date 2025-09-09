package com.benyaminrasouli.habitaway.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.benyaminrasouli.habitaway.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar {
        TextButton(onClick = { navController.navigate(Screen.Main.route) }) { Text("خانه") }
        TextButton(onClick = { navController.navigate(Screen.Habits.route) }) { Text("عادات") }
        TextButton(onClick = { navController.navigate(Screen.StudyRoom.route) }) { Text("سالن") }
        TextButton(onClick = { navController.navigate(Screen.Profile.route) }) { Text("پروفایل") }
    }
}

@Composable
fun FabButton(navController: NavController) {
    FloatingActionButton(onClick = { navController.navigate(Screen.CreateHabit.route) }) {
        Text("+")
    }
}
