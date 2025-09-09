package com.benyaminrasouli.habitaway.data.repo

import com.benyaminrasouli.habitaway.data.local.UserDao
import com.benyaminrasouli.habitaway.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        // جلوگیری از ثبت ایمیل تکراری
        val existing = userDao.getByEmail(user.email)
        if (existing != null) throw IllegalStateException("این ایمیل قبلاً ثبت شده است")
        userDao.insert(user)
    }

    suspend fun loginByEmail(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getByEmail(email)
    }
}
