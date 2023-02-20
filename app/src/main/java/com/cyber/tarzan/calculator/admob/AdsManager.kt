package com.cyber.tarzan.calculator.admob

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import com.google.android.gms.ads.*


class AdsManager {
    private val TAG: String = "testtag"

    fun showAdMobBanner(context: Context?, activity: Activity?, bannerLayout: LinearLayout) {
        val adView = AdView(activity!!)
        adView.adSize = getAdSize(activity)
        adView.adUnitId = AdIds.getAdmobBannerId().toString()
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        bannerLayout.addView(adView)
        adView.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                Log.e(TAG, "onAdClosed: ")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.e(TAG, "onAdFailedToLoad: $loadAdError")
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    Log.e(TAG, "onAdOpened: ")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.e(TAG, "onAdLoaded: ")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.e(TAG, "onAdClicked: ")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.e(TAG, "onAdImpression: ")
                }
            }
    }


    private fun getAdSize(context:Activity): AdSize? {
        val display = context.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context.applicationContext, adWidth)
    }

    interface AdmobAdInterface {
        fun onAdClosed()
    }

    companion object {
        val showAdMobBanner: AdsManager? = null
        private var mInstance: AdsManager? = null
        val instance: AdsManager?
            get() {
                if (mInstance == null) {
                    mInstance = AdsManager()
                }
                return mInstance
            }
    }


}
