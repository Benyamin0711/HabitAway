package com.benyaminrasouli.habitaway.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.benyaminrasouli.habitaway.ui.components.BottomBarWithFab

@Composable
fun StudyRoomScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBarWithFab(navController) },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("این صفحه سالن مطالعه است")
        }
    }
}
