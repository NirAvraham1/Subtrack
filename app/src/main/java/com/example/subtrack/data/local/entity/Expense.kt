package com.example.subtrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class ExpenseFrequency {
    MONTHLY, YEARLY
}

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val category: String,
    val frequency: ExpenseFrequency,

    val dateAdded: Long = System.currentTimeMillis(),
    val startDate: Long,
    val endDate: Long? = null,
    val renewalDate: Long
)
