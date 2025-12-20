package com.example.subtrack.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAdManager {
    private var mInterstitialAd: InterstitialAd? = null

    // מזהה בדיקה של גוגל לוידאו (Interstitial Test ID)
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // פונקציה לטעינת המודעה לזיכרון (נקרא לה כשהאפליקציה עולה)
    fun loadAd(context: Context) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    // פונקציה להצגת המודעה (נקרא לה כשלוחצים Save)
    fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (mInterstitialAd != null) {
            // אם יש מודעה מוכנה - נגדיר מה קורה כשסוגרים אותה
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null // מנקים את המודעה שהוצגה
                    loadAd(activity) // טוענים אחת חדשה לפעם הבאה
                    onAdDismissed() // מבצעים את הפעולה (חזרה למסך הבית)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    mInterstitialAd = null
                    onAdDismissed() // אם נכשל, לא תוקעים את המשתמש
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            // אם המודעה לא מוכנה (למשל אין אינטרנט), פשוט ממשיכים
            loadAd(activity) // מנסים לטעון לפעם הבאה
            onAdDismissed()
        }
    }
}