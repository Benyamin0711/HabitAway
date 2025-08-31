package com.cpx.habitaway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cpx.habitaway.navigation.NavGraph
import com.cpx.habitaway.ui.theme.HabitAwayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitAwayTheme {
                NavGraph()
            }
        }
    }
}
