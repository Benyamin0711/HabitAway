@file:OptIn(ExperimentalMaterial3Api::class)

package com.cpx.habitaway.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpx.habitaway.R
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    //userName: String = "بنیامین",
    habits: List<Habit> = sampleHabits
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent {
                scope.launch { drawerState.close() }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.habit_logo),
                            contentDescription = "لوگو",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF6200EE),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { /* اعلان‌ها */ }) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "اعلان‌ها",
                                tint = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "منو",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { /* افزودن عادت */ }) {
                    Icon(Icons.Default.Add, contentDescription = "افزودن عادت")
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "خانه") },
                        selected = true,
                        onClick = { /* خانه */ }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "پروفایل") },
                        selected = false,
                        onClick = { /* پروفایل */ }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text("امروز وقتشه که یه قدم دیگه برداری!", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { calculateProgress(habits) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("عادت‌های امروز:", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(habits) { habit ->
                        HabitItem(habit)
                        HorizontalDivider(
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(onClose: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("منو", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "تنظیمات",
            modifier = Modifier
                .clickable { onClose() }
                .padding(8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "درباره ما",
            modifier = Modifier
                .clickable { onClose() }
                .padding(8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun HabitItem(habit: Habit) {
    var isChecked by remember { mutableStateOf(habit.isCompleted) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                // اینجا می‌تونی وضعیت جدید رو ذخیره کنی
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(habit.name, fontSize = 16.sp)
            Text("زمان: ${habit.time}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

data class Habit(val name: String, val time: String, val isCompleted: Boolean)

val sampleHabits = listOf(
    Habit("مطالعه ۳۰ دقیقه", "08:00", false),
    Habit("ورزش صبحگاهی", "09:00", true),
    Habit("تمرین کدنویسی", "10:00", false)
)

fun calculateProgress(habits: List<Habit>): Float {
    if (habits.isEmpty()) return 0f
    val completed = habits.count { it.isCompleted }
    return completed.toFloat() / habits.size
}
