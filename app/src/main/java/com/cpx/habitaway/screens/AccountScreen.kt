package com.cpx.habitaway.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun AccountScreen(navController: NavController) {
    Column(Modifier.fillMaxSize(), Arrangement.Center) {
        Text("ورود / خروج حساب کاربری")
    }
}
