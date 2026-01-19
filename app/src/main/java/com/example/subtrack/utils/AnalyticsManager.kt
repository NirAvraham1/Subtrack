package com.example.subtrack.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    // Event 1: משתמש צפה במסך המנויים (כדי לבדוק מאוחר יותר מי לא קנה)
    fun logSubscriptionScreenView() {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Subscription_Page")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "SettingsScreen")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    // Event 2: משתמש חינמי ניסה ללחוץ על ה-AI (Upsell Opportunity)
    fun logFreeUserClickedAI() {
        val params = Bundle().apply {
            putString("feature_name", "AI_Advisor")
            putString("user_status", "free_tier")
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "locked_feature_click")
        }
        firebaseAnalytics.logEvent("upsell_attempt", params)
    }

    // Event 3: הוספת הוצאה חדשה (עם פרמטר של הקטגוריה והמחיר)
    fun logExpenseAdded(category: String, amount: Double) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
            putDouble(FirebaseAnalytics.Param.VALUE, amount)
            putString(FirebaseAnalytics.Param.CURRENCY, "ILS")
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "expense_item")
        }
        firebaseAnalytics.logEvent("add_expense_complete", params)
    }
}