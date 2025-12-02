package com.example.subtrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.subtrack.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // --- שינוי 1: שליפת הוצאות פעילות בלבד ---
    // התנאי: (אין תאריך סיום) או (תאריך הסיום גדול מהזמן שנשלח)
    @Query("SELECT * FROM expenses WHERE endDate IS NULL OR endDate > :currentTimeMillis ORDER BY renewalDate ASC")
    fun getActiveExpenses(currentTimeMillis: Long): Flow<List<Expense>>

    // --- שינוי 2: שליפת היסטוריה ---
    // התנאי: (יש תאריך סיום) וגם (תאריך הסיום קטן או שווה לזמן שנשלח)
    @Query("SELECT * FROM expenses WHERE endDate IS NOT NULL AND endDate <= :currentTimeMillis ORDER BY endDate DESC")
    fun getHistoryExpenses(currentTimeMillis: Long): Flow<List<Expense>>

    // --- שינוי 3: שליפת הכל (לשימוש עתידי של ה-AI) ---
    @Query("SELECT * FROM expenses")
    fun getAllExpensesRaw(): Flow<List<Expense>>


    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // שאילתה לבדיקת הוצאות בטווח תאריכים מסוים (למשל: מחר בבוקר עד מחר בלילה)
    @Query("SELECT * FROM expenses WHERE renewalDate >= :startDate AND renewalDate <= :endDate")
    suspend fun getExpensesInDateRange(startDate: Long, endDate: Long): List<Expense>
}