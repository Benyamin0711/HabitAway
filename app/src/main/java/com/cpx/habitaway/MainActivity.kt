package com.cpx.habitaway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cpx.habitaway.screens.MainScreen
import com.cpx.habitaway.ui.theme.HabitAwayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitAwayTheme {
                MainScreen()
            }
        }
    }
}