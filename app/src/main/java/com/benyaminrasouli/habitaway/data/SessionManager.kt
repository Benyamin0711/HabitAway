package com.benyaminrasouli.habitaway.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("user_email")
    }

    suspend fun saveUser(email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
        }
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { prefs: Preferences ->
        prefs[KEY_EMAIL]
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs: Preferences ->
        prefs[KEY_EMAIL] != null
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
