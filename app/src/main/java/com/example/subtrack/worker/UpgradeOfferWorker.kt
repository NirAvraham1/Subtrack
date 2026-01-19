package com.example.subtrack.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.subtrack.utils.NotificationHelper

class UpgradeOfferWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        NotificationHelper.showNotification(
            applicationContext,
            title = "Unlock AI Insights! 🤖",
            message = "Upgrade to Ultimate AI and let AI manage your budget. Tap to see the offer!",
            notificationId = 102
        )
        return Result.success()
    }
}