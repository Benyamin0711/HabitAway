@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.benyaminrasouli.habitaway.screens

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.benyaminrasouli.habitaway.ui.components.BottomBarWithFab
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import com.benyaminrasouli.habitaway.stories.InMemoryStoryRepository
import com.benyaminrasouli.habitaway.stories.StoriesBar
import com.benyaminrasouli.habitaway.stories.sampleInstagramStyleStories


data class Book(val id: String, val title: String, val cover: String)
data class Tool(val id: String, val title: String, val icon: ImageVector)

@Composable
fun MainScreen(
    navController: NavController,
    titleContent: (@Composable () -> Unit)? = null,
    userName: String = "Beny",
    isOnline: Boolean = true,
    onExitApp: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val activity = LocalContext.current as? Activity
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val drawerItems = listOf(
        DrawerItem("خانه", Icons.Filled.Home, "home"),
        DrawerItem("پروفایل", Icons.Filled.Person, "profile"),
        DrawerItem("تنظیمات", Icons.Filled.Settings, "settings"),
        DrawerItem("خروج", Icons.AutoMirrored.Filled.ExitToApp, "logout")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    Text(userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (isOnline) "آنلاین" else "آفلاین", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                // Items
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    drawerItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    // مسیر‌ها را اینجا هندل کن
                                    if (item.route == "logout") {
                                        onExitApp?.invoke() ?: run { activity?.finish() }
                                    } else {
                                        try {
                                            navController.navigate(item.route) {
                                                launchSingleTop = true
                                            }
                                        } catch (_: Exception) { /* امن باشیم اگر route نبود */
                                        }
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(item.icon, contentDescription = item.title)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(item.title, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "منو")
                        }
                    },
                    title = {
                        if (titleContent != null) {
                            titleContent()
                        } else {
                            Text("HabitAway", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* اعلان‌ها */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = "اعلان‌ها")
                        }
                    }
                )
            },
            bottomBar = { BottomBarWithFab(navController) },
            floatingActionButtonPosition = FabPosition.Center,
        ) { innerPadding ->
            HomeContent(
                modifier = Modifier.padding(innerPadding),
                navController = navController// پاس دادن ناو برای CTAهای استوری
            )
        }
    }
}

@Composable
fun HomeContent(modifier: Modifier = Modifier, navController: NavController) {
    // کتاب‌ها و ابزارها مثل قبلاً
    val atomicHabit = List(1){
        Book(
            "1",
            "Atomic Habit",
            "https://images.squarespace-cdn.com/content/v1/59c82ac46f4ca30b86d179bf/1706362642426-BI3J8PJ5LRJNO8H7WFV4/119.bookreview.AtomicHabits.jpg"
        )
    }
    val fiveamclub = List(1){
        Book(
            "2",
            "5AM Club",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3GIzpvBUMhDffcs54Z5T-ejIq-rNuhwt3OguNCwiKwpALBtwZD3cQTKG9yEqz2lFuPmw&usqp=CAU")
    }
    val deepwork = List(1){
        Book(
            "3",
            "Deep Work",
            "https://serajbookshop.com/wp-content/uploads/2022/01/deep-work-440x440.jpg")
    }

    val tools = listOf(
        Tool("pomodoro", "پومودورو", Icons.Default.Timer),
        Tool("meditation", "مدیتیشن", Icons.Default.SelfImprovement),
        Tool("timer", "تایمر", Icons.Default.AccessTime),
        Tool("frequencies", "فرکانس‌ها", Icons.Default.GraphicEq),
        Tool("breathing", "تنفس", Icons.Default.Air),
        Tool("journal", "ژورنال", Icons.Default.Create),
    )

    // repo استوری رو اینجا می‌سازیم تا بعدا بتونی به Room/Remote تغییر بدی
    val repo = remember { InMemoryStoryRepository(sampleInstagramStyleStories()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // استوری‌ها: حالا از ماژول حرفه‌ای استفاده می‌کنیم
        item {
            StoriesBar(
                repo = repo,
                onNavigate = { route ->
                    try { navController.navigate(route) { launchSingleTop = true } } catch (_: Exception) {}
                }
            )
        }

        // کتاب‌ها
        item {
            SectionHeader("کتاب‌ها")
            Spacer(Modifier.height(8.dp))
            BooksSection(books = atomicHabit + fiveamclub + deepwork)

        }

        // ابزارها
        item {
            SectionHeader("ابزارها")
            Spacer(Modifier.height(8.dp))
            ToolsSection(tools)
        }
    }
}

// *************************
// بقیه‌ی کامپوننت‌ها (BooksSection, ToolsSection, SectionHeader) دقیقا همون‌ چیزِ قبلی‌اند
// من اینجا همان کد تو رو حفظ کردم — لازم نیست تغییری بدی
// *************************

@Composable
fun BooksSection(books: List<Book>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(books) { book ->
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(140.dp)
                    .height(200.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(book.cover)
                            .crossfade(true)
                            .build(),
                        contentDescription = book.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        book.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = { /* شروع مطالعه */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("شروع")
                    }
                }
            }
        }
    }
}

@Composable
fun ToolsSection(tools: List<Tool>) {
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(17.dp, alignment = Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(17.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        tools.forEach { tool ->
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(100.dp)
                    .height(90.dp)
                    .clickable { /* رفتن به ابزار */ },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(tool.icon, contentDescription = tool.title, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(tool.title, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) { Text("بیشتر") }
        }
    }
}

data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)
