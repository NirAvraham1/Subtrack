package com.example.subtrack.utils

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.subtrack.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun NativeAdCard() {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    // טעינת המודעה
    LaunchedEffect(Unit) {
        val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110") // Test Native ID
            .forNativeAd { ad ->
                nativeAd = ad
            }
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    // הצגת המודעה
    if (nativeAd != null) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            factory = { ctx ->
                val view = LayoutInflater.from(ctx).inflate(R.layout.native_ad_layout, null) as NativeAdView
                populateNativeAdView(nativeAd!!, view)
                view
            }
        )
    }
}

// פונקציית עזר לחיבור הנתונים ל-XML
private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // 1. חיבור המשתנים לרכיבים ב-XML
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

    // 2. הצבת התוכן האמיתי מהמודעה
    (adView.headlineView as TextView).text = nativeAd.headline
    (adView.bodyView as TextView).text = nativeAd.body
    (adView.callToActionView as Button).text = nativeAd.callToAction

    if (nativeAd.icon != null) {
        (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
        adView.iconView?.visibility = View.VISIBLE
    } else {
        adView.iconView?.visibility = View.GONE
    }

    // 3. רישום המודעה (כדי שגוגל ידעו שהצגנו אותה)
    adView.setNativeAd(nativeAd)
}