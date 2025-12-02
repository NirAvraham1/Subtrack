package com.example.subtrack.data.local

import androidx.room.TypeConverter
import com.example.subtrack.data.local.entity.ExpenseFrequency

class Converters {

    @TypeConverter
    fun fromFrequency(value: ExpenseFrequency): String {
        return value.name
    }

    @TypeConverter
    fun toFrequency(value: String): ExpenseFrequency {
        return ExpenseFrequency.valueOf(value)
    }
}