package com.cpx.habitaway.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cpx.habitaway.navigation.Screen
import com.cpx.habitaway.R

@Composable
fun BottomBarWithFab(navController: NavController) {
    Box {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // آیکن‌های سمت چپ
                IconButton(onClick = { navController.navigate(Screen.CreateHabit.route) }) {
                    Icon(Default.Add, contentDescription = "Add Habit")
                }
                IconButton(onClick = { navController.navigate(Screen.Habits.route) }) {
                    Icon(Default.Menu, contentDescription = "عادات")
                }

                Spacer(modifier = Modifier.width(65.dp)) // فاصله برای FAB وسط

                // آیکن‌های سمت راست
                IconButton(onClick = { navController.navigate(Screen.StudyRoom.route) }) {
                    Icon(Default.Create, contentDescription = "سالن مطالعه")
                }
                IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                    Icon(Default.Person, contentDescription = "پروفایل")
                }
            }
        }

        // FAB وسط
        FloatingActionButton(
            onClick = { navController.navigate(Screen.Main.route) },
            modifier = Modifier
                .size(75.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.habit_logo),
                contentDescription = "خانه",
                modifier = Modifier.size(75.dp)
            )
        }
    }
}
