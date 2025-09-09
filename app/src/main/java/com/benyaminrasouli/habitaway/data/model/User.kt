package com.benyaminrasouli.habitaway.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,     // برای ثبت‌نام
    val email: String,        // برای لاگین (AuthScreen تو با ایمیل لاگین می‌کند)
    val password: String      // فعلاً ساده؛ بعداً هش می‌کنیم
)

