package com.example.subtrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.dao.ExpenseDao
import com.example.subtrack.data.local.entity.Expense
import com.example.subtrack.data.local.entity.ExpenseFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class CategorySummary(
    val categoryName: String,
    val totalYearlyAmount: Double
)

@HiltViewModel
class YearlySummaryViewModel @Inject constructor(
    dao: ExpenseDao
) : ViewModel() {

    val categories = dao.getAllExpensesRaw().map { expenseList ->
        val now = System.currentTimeMillis()

        expenseList
            // --- התיקון הקריטי: סינון הוצאות שהסתיימו ---
            // נשמור רק את מה שפעיל: (אין תאריך סיום) או (תאריך הסיום הוא בעתיד)
            .filter { expense ->
                expense.endDate == null || expense.endDate > now
            }
            .groupBy { it.category.trim().lowercase() }
            .map { (normalizedCategoryName, expenses) ->

                val total = expenses.sumOf { expense ->
                    calculateProjectedYearlyCost(expense, now)
                }

                val displayName = normalizedCategoryName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }

                CategorySummary(displayName, total)
            }
            .sortedByDescending { it.totalYearlyAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // עדכנתי את הפונקציה לקבל את 'now' מבחוץ כדי למנוע קריאות מיותרות למערכת
    private fun calculateProjectedYearlyCost(expense: Expense, now: Long): Double {

        // 1. אם התשלום הראשון כבר עבר (מנוי רגיל)
        if (expense.firstPaymentDate <= now) {
            return when (expense.frequency) {
                ExpenseFrequency.MONTHLY -> expense.amount * 12
                ExpenseFrequency.YEARLY -> expense.amount
            }
        }

        // 2. אם אנחנו בתוך תקופת ניסיון (התשלום הראשון בעתיד)
        val monthsUntilPayment = getMonthsDifference(now, expense.firstPaymentDate)

        if (monthsUntilPayment >= 12) return 0.0

        return when (expense.frequency) {
            ExpenseFrequency.MONTHLY -> {
                val payingMonths = 12 - monthsUntilPayment
                expense.amount * payingMonths
            }
            ExpenseFrequency.YEARLY -> {
                expense.amount
            }
        }
    }

    private fun getMonthsDifference(startDate: Long, endDate: Long): Int {
        val startCal = Calendar.getInstance().apply { timeInMillis = startDate }
        val endCal = Calendar.getInstance().apply { timeInMillis = endDate }

        val yearDiff = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)
        val monthDiff = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)

        return (yearDiff * 12) + monthDiff
    }
}