package com.example.subtrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.UserPreferences
import com.example.subtrack.utils.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    init {
        // ברגע שה-ViewModel נוצר, סימן שהמשתמש נכנס למסך
        analyticsManager.logSubscriptionScreenView()
    }

    // קורא את המנוי הנוכחי כדי שנדע מה לסמן במסך
    val currentPlan = userPreferences.subscriptionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "free")

    // פונקציה לשמירת מנוי חדש
    fun selectPlan(planId: String) {
        viewModelScope.launch {
            userPreferences.saveSubscription(planId)
        }
    }
}