package com.example.subtrack.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.subtrack.R
import com.example.subtrack.data.local.UserPreferences
import com.example.subtrack.data.local.dao.ExpenseDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val expenseDao: ExpenseDao,
    private val userPreferences: UserPreferences
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. בדיקה: האם המשתמש בכלל רוצה התראות?
        val isEnabled = userPreferences.notificationsFlow.first()
        if (!isEnabled) {
            return Result.success()
        }

        // --- שינוי: קריאת מספר הימים מראש ---
        val daysInAdvance = userPreferences.notificationAdvanceDaysFlow.first()

        // 2. חישוב תאריך היעד (היום + ימי ההתראה)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysInAdvance) // <-- השינוי: דינמי במקום קבוע

        // התחלת יום היעד
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfTargetDay = calendar.timeInMillis

        // סוף יום היעד
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfTargetDay = calendar.timeInMillis

        // 3. שליפה מהדאטה-בייס
        val expensesTarget = expenseDao.getExpensesInDateRange(startOfTargetDay, endOfTargetDay)

        // 4. אם יש תוצאות -> שלח התראה
        if (expensesTarget.isNotEmpty()) {
            val expenseNames = expensesTarget.joinToString(", ") { it.name }
            val totalAmount = expensesTarget.sumOf { it.amount }

            // טקסט מותאם
            val timeText = when (daysInAdvance) {
                1 -> "Tomorrow"
                7 -> "In one week"
                30 -> "In one month"
                else -> "In $daysInAdvance days"
            }

            showNotification(
                "Upcoming Renewals",
                "$timeText: $expenseNames will renew (Total: $totalAmount ₪)"
            )
        }

        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        val channelId = "subtrack_renewals"
        val notificationId = 1001

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Subscription Renewals"
            val descriptionText = "Notifications for upcoming subscriptions"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.subtrack_logo)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
        }
    }
}