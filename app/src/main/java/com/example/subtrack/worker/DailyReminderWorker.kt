package com.example.subtrack.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.subtrack.utils.NotificationHelper

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // כאן הקוד שרץ ברקע
        NotificationHelper.showNotification(
            applicationContext,
            title = "Did you spend money today? 💸",
            message = "Take a second to log your expenses and stay on track!",
            notificationId = 101
        )
        return Result.success()
    }
}