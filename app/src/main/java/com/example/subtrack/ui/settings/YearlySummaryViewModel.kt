package com.example.subtrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.dao.ExpenseDao
import com.example.subtrack.data.local.entity.ExpenseFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    // --- השינוי כאן: קריאה ל-getAllExpensesRaw במקום getAllExpenses ---
    val categories = dao.getAllExpensesRaw().map { expenseList ->
        expenseList
            .groupBy { it.category.trim().lowercase() }
            .map { (normalizedCategoryName, expenses) ->

                val total = expenses.sumOf { expense ->
                    when (expense.frequency) {
                        ExpenseFrequency.MONTHLY -> expense.amount * 12
                        ExpenseFrequency.YEARLY -> expense.amount
                    }
                }

                val displayName = normalizedCategoryName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }

                CategorySummary(displayName, total)
            }
            .sortedByDescending { it.totalYearlyAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}