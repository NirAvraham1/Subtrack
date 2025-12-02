package com.example.subtrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.dao.ExpenseDao
import com.example.subtrack.data.local.entity.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    dao: ExpenseDao
) : ViewModel() {

    // אנחנו שולחים את הזמן הנוכחי כדי לקבל רק את מה שפג תוקפו
    val historyExpenses: StateFlow<List<Expense>> = dao.getHistoryExpenses(System.currentTimeMillis())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}