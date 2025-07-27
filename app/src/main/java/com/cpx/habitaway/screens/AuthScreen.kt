package com.cpx.habitaway.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cpx.habitaway.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

@Composable
fun AuthScreen(navController: NavHostController) {
    var isLogin by remember { mutableStateOf(true) }

    Crossfade(targetState = isLogin, label = "") { showLogin ->
        if (showLogin) {
            LoginForm(onSwitch = { isLogin = false }, navController = navController)
        } else {
            RegisterForm(onSwitch = { isLogin = true }, navController = navController)
        }
    }
}

fun convertPersianDigitsToEnglish(input: String): String {
    val persianDigits = arrayOf('۰','۱','۲','۳','۴','۵','۶','۷','۸','۹')
    val englishDigits = arrayOf('0','1','2','3','4','5','6','7','8','9')

    var output = input
    for (i in persianDigits.indices) {
        output = output.replace(persianDigits[i], englishDigits[i])
    }
    return output
}

fun passwordStrength(password: String): Pair<String, Color> {
    val cleanedPassword = convertPersianDigitsToEnglish(password)

    var strength = 0
    if (cleanedPassword.length >= 8) strength++
    if (cleanedPassword.matches(".*[A-Z].*".toRegex())) strength++
    if (cleanedPassword.matches(".*[0-9].*".toRegex())) strength++
    if (cleanedPassword.matches(".*[!@#\$%^&*(),.?\":{}|<>].*".toRegex())) strength++

    return when (strength) {
        0, 1 -> "ضعیف" to Color.Red
        2 -> "متوسط" to Color(0xFFFFA500)
        3, 4 -> "قوی" to Color.Green
        else -> "نامشخص" to Color.Gray
    }
}

@Composable
fun RegisterForm(onSwitch: () -> Unit, navController: NavHostController) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val motivationalTexts = listOf(
            "هر روز یک فرصت تازه است.",
            "تو از چیزی که فکر می‌کنی قوی‌تری.",
            "شروع کن، حتی اگر کامل نیست.",
            "باور داشته باش که می‌تونی.",
            "تغییر از درون شروع می‌شه.",
            "به عنوان قاعده کلی، هر قدر به فردی نزدیک تر باشیم، \n بیش تر در معرض تقلید عادت های او قرار داریم. "

        )
        val randomMotivation = remember { motivationalTexts.random() }

        val BTitr = FontFamily(Font(R.font.btitr))

        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }

        var password by remember { mutableStateOf("") }
        var passwordStrengthText by remember { mutableStateOf("") }
        var passwordStrengthColor by remember { mutableStateOf(Color.Unspecified) }

        var confirmPassword by remember { mutableStateOf("") }
        var confirmError by remember { mutableStateOf<String?>(null) }

        var username by remember { mutableStateOf("") }
        var usernameError by remember { mutableStateOf<String?>(null) }

        var loading by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        val iconSize = 24.dp
        val backgroundImage: Painter = painterResource(id = R.drawable.login_img)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = backgroundImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.9f
            )

            Column(
                modifier = Modifier
                    .size(width = 480.dp, height = 700.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(randomMotivation, color = Color(0xFF5CEBCC), fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp), fontFamily = BTitr)

                Text("ثبت‌نام", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontFamily = BTitr)
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = null
                    },
                    label = { Text("نام کاربری") },
                    textStyle = TextStyle(
                        fontFamily = BTitr,),
                    shape = RoundedCornerShape(30.dp),
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.user), contentDescription = null, tint = Color.Black, modifier = Modifier.size(iconSize))
                    },
                    isError = usernameError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (usernameError != null) Text(usernameError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("ایمیل") },
                    textStyle = TextStyle(
                        fontFamily = BTitr,),
                    shape = RoundedCornerShape(30.dp),
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.email), contentDescription = null, tint = Color.Black, modifier = Modifier.size(iconSize))
                    },
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) Text(emailError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        val (text, color) = passwordStrength(it)
                        passwordStrengthText = text
                        passwordStrengthColor = color
                        confirmError = null
                    },
                    label = { Text("رمز عبور") },
                    textStyle = TextStyle(
                        fontFamily = BTitr,),
                    shape = RoundedCornerShape(30.dp),
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.password), contentDescription = null, tint = Color.Black, modifier = Modifier.size(iconSize))
                    },
                    trailingIcon = {
                        val iconId = if (passwordVisible) R.drawable.eye_open else R.drawable.eye_closed
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(painter = painterResource(id = iconId), contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmError = null
                    },
                    label = { Text("تأیید رمز عبور") },
                    textStyle = TextStyle(
                        fontFamily = BTitr,),
                    shape = RoundedCornerShape(30.dp),
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.password), contentDescription = null, tint = Color.Black, modifier = Modifier.size(iconSize))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (confirmError != null) Text(confirmError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))

                Spacer(modifier = Modifier.height(8.dp))

                Text("قدرت رمز: $passwordStrengthText", color = passwordStrengthColor, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        var valid = true
                        loading = true

                        if (!email.trim().endsWith("@gmail.com")) {
                            emailError = "ایمیل باید با @gmail.com تمام شود"
                            valid = false
                        }
                        if (passwordStrengthText == "ضعیف") {
                            confirmError = "رمز عبور ضعیف میباشد"
                            valid = false
                        }
                        if (password != confirmPassword) {
                            confirmError = "رمز عبور و تأیید آن مطابقت ندارند"
                            valid = false
                        }
                        if (username.isBlank()) {
                            usernameError = "نام کاربری نمی‌تواند خالی باشد"
                            valid = false
                        }

                        if (valid) {
                            // navController.navigate("next_screen")
                        }
                        loading = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("ثبت‌نام",fontFamily = BTitr)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onSwitch, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("حساب دارید؟ وارد شوید",fontFamily = BTitr)
                }
            }
        }
    }
}



@Composable
fun LoginForm(onSwitch: () -> Unit, navController: NavHostController) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }

        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        val iconSize = 24.dp

        val BTitr = FontFamily(Font(R.font.btitr))

        val backgroundImage: Painter = painterResource(id = R.drawable.login_img)

        var loading by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val motivationalTexts = listOf(
            "هر روز یک فرصت تازه است.",
            "تو از چیزی که فکر می‌کنی قوی‌تری.",
            "شروع کن، حتی اگر کامل نیست.",
            "باور داشته باش که می‌تونی.",
            "تغییر از درون شروع می‌شه.",
            "به عنوان قاعده کلی، هر قدر به فردی نزدیک تر باشیم، \n بیش تر در معرض تقلید عادت های او قرار داریم. "

        )
        val randomMotivation = remember { motivationalTexts.random() }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = backgroundImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.9f
            )

            Column(
                modifier = Modifier
                    .size(width = 480.dp, height = 600.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    fontFamily = BTitr,
                    text = randomMotivation,
                    color = Color(0xFF5CEBCC),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally) // فقط این کامپوننت
                        .padding(bottom = 16.dp)
                )




                Text(
                    text = "ورود",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontFamily = BTitr
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("ایمیل") },
                    textStyle = TextStyle(
                        fontFamily = BTitr
                    ),
                    shape = RoundedCornerShape(30.dp),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.email),
                            contentDescription = "ایمیل",
                            tint = Color.Black,
                            modifier = Modifier.size(iconSize)
                        )
                    },
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("رمز عبور") },
                    textStyle = TextStyle(
                        fontFamily = BTitr
                    ),
                    shape = RoundedCornerShape(30.dp),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.password),
                            contentDescription = "رمز عبور",
                            tint = Color.Black,
                            modifier = Modifier.size(iconSize)
                        )
                    },
                    trailingIcon = {
                        val iconId = if (passwordVisible) R.drawable.eye_open else R.drawable.eye_closed
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(painter = painterResource(id = iconId), contentDescription = "Toggle Password Visibility")
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        var valid = true
                        loading = true

                        if (!email.trim().endsWith("@gmail.com")) {
                            emailError = "ایمیل باید با @gmail.com تمام شود"
                            valid = false
                        }

                        if (valid) {
                            coroutineScope.launch {
                                // اینجا بررسی وجود کاربر واقعی در دیتابیس یا API شما انجام می‌گیرد
                                delay(1000) // شبیه‌سازی تأخیر پاسخ سرور
                                loading = false

                                if (email == "test@gmail.com" && password == "Test1234") {
                                    snackbarHostState.showSnackbar("ورود موفقیت‌آمیز بود")
                                    // navController.navigate("home")
                                } else {
                                    snackbarHostState.showSnackbar("ایمیل یا رمز نادرست است")
                                }
                            }
                        } else {
                            loading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("ورود", fontFamily = BTitr)

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        // عملکرد ورود با گوگل
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google Sign-In",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ورود با گوگل", color = Color.White, fontFamily = BTitr)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onSwitch,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("حساب ندارید؟ ثبت‌نام کنید",fontFamily = BTitr)
                }
            }

            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

