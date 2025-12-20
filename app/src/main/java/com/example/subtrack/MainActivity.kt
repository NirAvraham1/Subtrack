package com.example.subtrack

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.subtrack.ui.AppNavigation
import com.example.subtrack.ui.theme.SubTrackTheme
import com.example.subtrack.utils.InterstitialAdManager // <-- הוספנו את האימפורט הזה
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

        // --- טעינת פרסומת וידאו לזיכרון (כדי שתהיה מוכנה לשמירה) ---
        InterstitialAdManager.loadAd(this)

        // בדיקה: אם אנדרואיד 13+ ואין לנו הרשאה -> נבקש אותה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            SubTrackTheme {
                AppNavigation()
            }
        }
    }
}