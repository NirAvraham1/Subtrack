package com.example.subtrack.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey // <-- הוספנו
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val SUBSCRIPTION_KEY = stringPreferencesKey("subscription_type")
    private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")

    // --- מפתח חדש: כמה ימים מראש להתריע (ברירת מחדל: 1) ---
    private val NOTIFICATION_ADVANCE_DAYS_KEY = intPreferencesKey("notification_advance_days")

    val subscriptionFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[SUBSCRIPTION_KEY] ?: "free" }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[NOTIFICATIONS_KEY] ?: false }

    // --- קריאת ימי ההתראה ---
    val notificationAdvanceDaysFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[NOTIFICATION_ADVANCE_DAYS_KEY] ?: 1 }

    suspend fun saveSubscription(type: String) {
        context.dataStore.edit { preferences -> preferences[SUBSCRIPTION_KEY] = type }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }

    // --- שמירת ימי ההתראה ---
    suspend fun setNotificationAdvanceDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_ADVANCE_DAYS_KEY] = days
        }
    }
}