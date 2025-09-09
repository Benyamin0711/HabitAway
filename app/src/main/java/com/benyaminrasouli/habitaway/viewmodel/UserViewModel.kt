package com.benyaminrasouli.habitaway.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.benyaminrasouli.habitaway.data.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // وضعیت ورود کاربر
    val isLoggedIn: Flow<Boolean> = sessionManager.isLoggedIn

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // اینجا لاجیک واقعی لاگین (API/Database) باید باشه
                if (email.isNotBlank() && password.isNotBlank()) {
                    sessionManager.saveUser(email) // ذخیره ایمیل
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("ایمیل یا پسورد خالیه")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "خطای ناشناخته")
            }
        }
    }

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // اینجا لاجیک رجیستر واقعی
                sessionManager.saveUser(email)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "خطای ثبت‌نام")
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _authState.value = AuthState.Idle
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
