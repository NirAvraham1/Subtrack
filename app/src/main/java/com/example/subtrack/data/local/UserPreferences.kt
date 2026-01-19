package com.example.subtrack.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
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
    private val NOTIFICATION_ADVANCE_DAYS_KEY = intPreferencesKey("notification_advance_days")


    private val KEY_RATE_LAST_SHOWN = longPreferencesKey("rate_last_shown")
    private val KEY_SHARE_LAST_SHOWN = longPreferencesKey("share_last_shown")

    private val KEY_HAS_USER_RATED = booleanPreferencesKey("has_user_rated")

    val subscriptionFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[SUBSCRIPTION_KEY] ?: "free" }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[NOTIFICATIONS_KEY] ?: false }

    val notificationAdvanceDaysFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[NOTIFICATION_ADVANCE_DAYS_KEY] ?: 1 }


    val hasUserRatedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_HAS_USER_RATED] ?: false }

    suspend fun saveSubscription(type: String) {
        context.dataStore.edit { preferences -> preferences[SUBSCRIPTION_KEY] = type }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun setNotificationAdvanceDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_ADVANCE_DAYS_KEY] = days
        }
    }


    suspend fun markAppAsRated() {
        context.dataStore.edit { preferences ->
            preferences[KEY_HAS_USER_RATED] = true
        }
    }

    suspend fun saveDialogDismissTime(dialogType: String) {
        context.dataStore.edit { preferences ->
            val key = if (dialogType == "rate") KEY_RATE_LAST_SHOWN else KEY_SHARE_LAST_SHOWN
            preferences[key] = System.currentTimeMillis()
        }
    }

    fun shouldShowDialog(dialogType: String): Flow<Boolean> = context.dataStore.data.map { preferences ->
        val key = if (dialogType == "rate") KEY_RATE_LAST_SHOWN else KEY_SHARE_LAST_SHOWN
        val lastShownTime = preferences[key] ?: 0L

        val fortyEightHoursInMillis = 48 * 60 * 60 * 1000L
        val currentTime = System.currentTimeMillis()

        if (lastShownTime == 0L) {
            true
        } else {
            currentTime - lastShownTime > fortyEightHoursInMillis
        }
    }
}