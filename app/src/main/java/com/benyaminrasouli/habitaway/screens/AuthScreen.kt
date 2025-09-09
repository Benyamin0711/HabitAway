package com.benyaminrasouli.habitaway.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.benyaminrasouli.habitaway.viewmodel.AuthState
import com.benyaminrasouli.habitaway.viewmodel.UserViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.benyaminrasouli.habitaway.navigation.Screen


@Composable
fun AuthScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }

    val state by userViewModel.authState.collectAsState()

    // ✅ گوش به تغییر state
    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Account.route) { inclusive = true } // لاگین صفحه حذف بشه
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isLogin) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (isLogin) userViewModel.loginUser(email, password)
                else userViewModel.registerUser(username, email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "Login" else "Register")
        }

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(if (isLogin) "No account? Register" else "Have account? Login")
        }

        Spacer(Modifier.height(8.dp))
        when (state) {
            is AuthState.Loading -> Text("در حال پردازش…")
            is AuthState.Success -> Text("موفقیت 🎉")
            is AuthState.Error -> Text(
                (state as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            else -> {}
        }
    }
}
