package com.example.subtrack.ui.add_expense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.dao.ExpenseDao
import com.example.subtrack.data.local.entity.Expense
import com.example.subtrack.data.local.entity.ExpenseFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    savedStateHandle: SavedStateHandle // זה רכיב של אנדרואיד שמחזיק את הפרמטרים מהניווט
) : ViewModel() {

    // משתנה ששומר את ההוצאה הנוכחית (אם אנחנו בעריכה)
    private val _expenseState = MutableStateFlow<Expense?>(null)
    val expenseState = _expenseState.asStateFlow()

    private var currentExpenseId: Int? = null

    init {
        // ברגע שה-ViewModel עולה, נבדוק אם קיבלנו ID
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
                _expenseState.value = it // מעדכן את ה-UI בנתונים הקיימים
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
        renewalDate: Long
    ) {
        val amountValue = amount.toDoubleOrNull() ?: 0.0

        // אם יש לנו ID קיים, נשתמש בו (Update). אם לא, זה יהיה 0 (Insert)
        val expense = Expense(
            id = currentExpenseId ?: 0,
            name = name,
            amount = amountValue,
            category = category,
            frequency = frequency,
            startDate = startDate,
            endDate = endDate,
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