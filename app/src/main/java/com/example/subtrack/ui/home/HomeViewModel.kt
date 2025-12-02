package com.example.subtrack.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.UserPreferences // <-- הוסף אימפורט
import com.example.subtrack.data.local.dao.ExpenseDao
import com.example.subtrack.data.local.entity.ExpenseFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dao: ExpenseDao,
    userPreferences: UserPreferences // <-- הזרקנו את זה (זה היה חסר)
) : ViewModel() {

    // --- הוספנו את זה: קריאת המנוי הנוכחי למסך הבית ---
    val currentPlan = userPreferences.subscriptionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "free")

    val expenses = dao.getActiveExpenses(System.currentTimeMillis())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMonthlyCost = expenses.map { list ->
        list.sumOf { expense ->
            when (expense.frequency) {
                ExpenseFrequency.MONTHLY -> expense.amount
                ExpenseFrequency.YEARLY -> expense.amount / 12
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}