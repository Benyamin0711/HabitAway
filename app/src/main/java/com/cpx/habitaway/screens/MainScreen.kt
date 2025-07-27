@file:OptIn(ExperimentalMaterial3Api::class)

package com.cpx.habitaway.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.cpx.habitaway.Classes.AuthViewModel
import com.cpx.habitaway.R
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

sealed class BottomNavScreen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector){
    object Home : BottomNavScreen("home", "خانه", Icons.Default.Home)
    object Profile : BottomNavScreen("profile", "پروفایل", Icons.Default.Person)
}

@Composable
fun MainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isLoggedIn = authViewModel.isLoggedIn


    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.80f),
        drawerContent = {
            DrawerContent (
                onClose = { scope.launch { drawerState.close() } },
                navController = navController,
                isLoggedIn = isLoggedIn
            )
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
                        containerColor = Color(0xFF673AB7),
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
            bottomBar = {
                CustomBottomBar(
                    onFabClick = {},
                    onItemClick = { title: String -> },
                    navController = navController,
                    isLoggedIn = isLoggedIn
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                UserCard()
            }
        }
    }
}

@Composable
fun DrawerContent(
    onClose: () -> Unit,
    navController: NavController,
    isLoggedIn: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxHeight().width(220.dp).padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEFEFEF)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_placeholder),
                        contentDescription = "avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Benyamin", fontSize = 18.sp, color = Color.Black)
                        Text("Online", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()

                DrawerItem("خانه", Icons.Default.Home, onClick = onClose)
                DrawerItem("پروفایل", Icons.Default.Person, onClick = {
                    onClose()
                    if (isLoggedIn){
                        navController.navigate("profile")
                    } else {
                        navController.navigate("Auth")
                    }
                })
                DrawerItem("تنظیمات", Icons.Default.Settings, onClick = onClose)
                DrawerItem("درباره ما", Icons.Default.Info, onClick = onClose)
                Spacer(modifier = Modifier.weight(1f))
                DrawerItem(title = "خروج", icon = Icons.Default.ExitToApp, onClick = { exitProcess(0) }, color = Color.Red)
            }
        }



        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun CustomBottomBar(
    onFabClick: () -> Unit,
    onItemClick: (String) -> Unit,
    navController: NavController,
    isLoggedIn : Boolean
){
    val item =listOf("پروفایل", "سالن مطالعه", "تلوزیون", "روتین")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(81.dp)
            .background(Color(0xFF1C1C2D))
    ){
        Row (modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            item.forEachIndexed { index, item ->
                if (index == 2){
                    Spacer(modifier = Modifier.width(64.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable{ onItemClick(item) }
                ){
                    if (item == "پروفایل"){
                        if (isLoggedIn){
                            navController.navigate("profile")
                        }else{
                            navController.navigate("Auth")
                            Image(
                                painter = painterResource(id = R.drawable.avatar_placeholder),
                                contentDescription = "item",
                                modifier = Modifier.size(24.dp)
                                    .clip(MaterialTheme.shapes.medium )
                            )
                        }

                    }else{
                        Icon(
                            painter = painterResource(
                                id = when (item){
                                    "تلوزیون" -> R.drawable.television
                                    "سالن مطالعه" -> R.drawable.book
                                    "روتین" -> R.drawable.check
                                    else -> R.drawable.check
                                }
                            ),
                            contentDescription = item,
                            tint = Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item, fontSize = 12.sp, color = Color.LightGray)
                }
            }
        }
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(65.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.habit_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(66.dp)
            )
        }
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, color = color)
    }
}

@Composable
fun UserCard(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1F2937), Color(0xFF111827)))
            )
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .padding(16.dp)
    )
    {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00BCD4)),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "League Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LVL 80",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "از خاکستر ها بر می خیزم",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
