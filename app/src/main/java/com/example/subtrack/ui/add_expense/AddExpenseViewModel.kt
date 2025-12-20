package com.example.subtrack.ui.add_expense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.UserPreferences // <-- חשוב!
import com.example.subtrack.data.local.dao.ExpenseDao
import com.example.subtrack.data.local.entity.Expense
import com.example.subtrack.data.local.entity.ExpenseFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val userPreferences: UserPreferences, // <-- הוספנו את זה לבנאי
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _expenseState = MutableStateFlow<Expense?>(null)
    val expenseState = _expenseState.asStateFlow()

    // --- התיקון: חשיפת המשתנה שהיה חסר ---
    val currentPlan = userPreferences.subscriptionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "free")

    private var currentExpenseId: Int? = null

    init {
        val expenseId = savedStateHandle.get<Int>("expenseId")
        if (expenseId != null && expenseId != -1) {
            loadExpense(expenseId)
        }
    }

    private fun loadExpense(id: Int) {
        viewModelScope.launch {
            val expense = expenseDao.getExpenseById(id)
            expense?.let {
                currentExpenseId = it.id
                _expenseState.value = it
            }
        }
    }

    fun saveExpense(
        name: String,
        amount: String,
        category: String,
        frequency: ExpenseFrequency,
        startDate: Long,
        endDate: Long?,
        renewalDate: Long,
        trialMonths: Int
    ) {
        val amountValue = amount.toDoubleOrNull() ?: 0.0

        val firstPaymentDate = if (trialMonths > 0) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = startDate
            calendar.add(Calendar.MONTH, trialMonths)
            calendar.timeInMillis
        } else {
            startDate
        }

        val expense = Expense(
            id = currentExpenseId ?: 0,
            name = name,
            amount = amountValue,
            category = category,
            frequency = frequency,
            startDate = startDate,
            endDate = endDate,
            firstPaymentDate = firstPaymentDate,
            renewalDate = renewalDate
        )

        viewModelScope.launch {
            expenseDao.insertExpense(expense)
        }
    }

    fun deleteExpense() {
        val currentExpense = _expenseState.value
        if (currentExpense != null) {
            viewModelScope.launch {
                expenseDao.deleteExpense(currentExpense)
            }
        }
    }
}