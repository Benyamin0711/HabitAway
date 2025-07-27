package com.cpx.habitaway.Classes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    // روش صحیح برای استفاده از MutableState در ViewModel
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: Boolean
        get() = _isLoggedIn.value

    // متدهای تغییر وضعیت
    fun login() { _isLoggedIn.value = true }
    fun logout() { _isLoggedIn.value = false }
}