package com.example.subtrack.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.subtrack.MainActivity
import com.example.subtrack.R

object NotificationHelper {

    private const val CHANNEL_ID = "subtrack_notifications"
    private const val CHANNEL_NAME = "SubTrack Reminders"

    // יצירת הערוץ (חובה באנדרואיד 8+)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Reminders and offers from SubTrack"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // שליחת ההתראה בפועל
    fun showNotification(context: Context, title: String, message: String, notificationId: Int) {
        // לחיצה על ההתראה תפתח את האפליקציה
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            // שנה את השורה הזו לשם של האייקון שיצרת כרגע:
            .setSmallIcon(R.drawable.ic_notification_logo)

            // אופציונלי: כאן אתה קובע באיזה צבע הוא יהיה כשהווילון פתוח (למשל ירוק של המותג שלך)
            .setColor(context.getColor(R.color.teal_200))

            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // במקרה שאין הרשאה, לא נרסק את האפליקציה
            e.printStackTrace()
        }
    }
}