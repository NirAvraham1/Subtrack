package com.example.subtrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.subtrack.data.local.UserPreferences
import com.example.subtrack.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val workManager: WorkManager
) : ViewModel() {

    val currentPlan = userPreferences.subscriptionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "free")

    val isNotificationsEnabled = userPreferences.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // --- חשיפת ימי ההתראה ל-UI ---
    val notificationAdvanceDays = userPreferences.notificationAdvanceDaysFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)

            val workName = "daily_renewal_check"

            if (enabled) {
                val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    workName,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
            } else {
                workManager.cancelUniqueWork(workName)
            }
        }
    }

    // --- פונקציה לעדכון הימים ---
    fun setNotificationAdvanceDays(days: Int) {
        viewModelScope.launch {
            userPreferences.setNotificationAdvanceDays(days)
        }
    }
}