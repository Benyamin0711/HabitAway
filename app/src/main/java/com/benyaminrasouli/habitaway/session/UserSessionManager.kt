package com.benyaminrasouli.habitaway.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userDataStore by preferencesDataStore("user_session")

class UserSessionManager(private val context: Context) {

    private val KEY_EMAIL = stringPreferencesKey("email")

    suspend fun save(email: String) {
        context.userDataStore.edit { it[KEY_EMAIL] = email }
    }

    val emailFlow: Flow<String?> = context.userDataStore.data.map { it[KEY_EMAIL] }

    suspend fun clear() {
        context.userDataStore.edit { it.remove(KEY_EMAIL) }
    }
}
