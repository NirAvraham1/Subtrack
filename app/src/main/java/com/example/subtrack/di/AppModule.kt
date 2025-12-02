package com.example.subtrack.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.subtrack.data.local.AppDatabase
import com.example.subtrack.data.local.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // המודול הזה יחיה כל עוד האפליקציה חיה
object AppModule {

    // פונקציה שמלמדת את Hilt איך ליצור את מסד הנתונים
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "subtrack_database" // שם הקובץ שיישמר בטלפון
        )
            .fallbackToDestructiveMigration() // מוחק ובונה מחדש אם משנים את הטבלה (נוח לפיתוח)
            .build()
    }

    // פונקציה שמלמדת את Hilt איך לספק את ה-DAO
    @Provides
    @Singleton
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}