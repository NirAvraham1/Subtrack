package com.example.subtrack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
// --- תוספות ל-WorkManager ---
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.subtrack.utils.NotificationHelper
import com.example.subtrack.workers.DailyReminderWorker
import com.example.subtrack.workers.UpgradeOfferWorker
// ----------------------------
import com.example.subtrack.ui.AppNavigation
import com.example.subtrack.ui.MainViewModel
import com.example.subtrack.ui.dialogs.RateAppDialog
import com.example.subtrack.ui.dialogs.ShareAppDialog
import com.example.subtrack.ui.dialogs.UpdateAppDialog
import com.example.subtrack.ui.theme.SubTrackTheme
import com.example.subtrack.utils.InterstitialAdManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // משגר לבקשת הרשאה
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // יש אישור!
        } else {
            // המשתמש סירב :(
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- אתחול פרסומות ---
        MobileAds.initialize(this) {}

        // --- טעינת פרסומת וידאו לזיכרון ---
        InterstitialAdManager.loadAd(this)

        // בדיקה: אם אנדרואיד 13+ ואין לנו הרשאה -> נבקש אותה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // --- תוספת חדשה: תזמון התראות שיווקיות (Marketing & Retention) ---
        // 1. יצירת ערוץ ההתראות (חובה)
        NotificationHelper.createNotificationChannel(this)

        // 2. תזמון תזכורת יומית (Habit Loop) - כל 24 שעות
        val dailyRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS) // יתחיל לרוץ בעוד שעה מעכשיו
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_reminder_marketing", // שם ייחודי כדי לא ליצור כפילויות
            ExistingPeriodicWorkPolicy.KEEP, // אם כבר קיים, תשמור עליו ואל תיצור חדש
            dailyRequest
        )

        // 3. תזמון הצעת שדרוג (Reactivation) - חד פעמי (לבדיקה: בעוד 10 שניות)
        val upgradeRequest = OneTimeWorkRequestBuilder<UpgradeOfferWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(upgradeRequest)
        // ---------------------------------------------------------------

        setContent {
            SubTrackTheme {
                // 1. יצירת ה-ViewModel לניהול הפופ-אפים
                val mainViewModel: MainViewModel = hiltViewModel()

                // 2. האזנה לסטייט: איזה דיאלוג צריך להציג עכשיו?
                val activeDialog by mainViewModel.activeDialog.collectAsState()

                // 3. הצגת האפליקציה הרגילה
                AppNavigation()

                // 4. שכבת הדיאלוגים
                when (activeDialog) {
                    MainViewModel.DialogType.RATE -> {
                        RateAppDialog(
                            onDismiss = {
                                // המשתמש לחץ "Not Now" - נציג שוב בעוד 48 שעות
                                mainViewModel.onDialogDismissed(MainViewModel.DialogType.RATE)
                            },
                            // התיקון נמצא כאן: הפונקציה מקבלת בוליאני (האם לפתוח חנות?)
                            onRateFinished = { shouldOpenStore ->
                                // קודם כל מסמנים ב-Preferences שהמשתמש דירג (כדי שלא יקפוץ יותר לעולם)
                                mainViewModel.onUserRated()

                                // רק אם המשתמש מרוצה (shouldOpenStore == true) נפתח את החנות
                                if (shouldOpenStore) {
                                    try {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                                    } catch (e: Exception) {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                                    }
                                }
                            }
                        )
                    }
                    MainViewModel.DialogType.SHARE -> {
                        ShareAppDialog(
                            onDismiss = { mainViewModel.onDialogDismissed(MainViewModel.DialogType.SHARE) },
                            onShareClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out SubTrack! It helps me save money on subscriptions. Download here: https://play.google.com/store/apps/details?id=$packageName")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
                                mainViewModel.onDialogDismissed(MainViewModel.DialogType.SHARE)
                            }
                        )
                    }
                    MainViewModel.DialogType.UPDATE -> {
                        UpdateAppDialog(
                            onDismiss = { mainViewModel.onDialogDismissed(MainViewModel.DialogType.UPDATE) },
                            onUpdateClick = {
                                try {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                                } catch (e: Exception) {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                                }
                                mainViewModel.onDialogDismissed(MainViewModel.DialogType.UPDATE)
                            }
                        )
                    }
                    null -> { /* לא מציגים כלום */ }
                }
            }
        }
    }
}