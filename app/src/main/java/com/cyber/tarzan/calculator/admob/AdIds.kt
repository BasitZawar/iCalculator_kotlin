package com.cyber.tarzan.calculator.admob

import com.cyber.tarzan.calculator.BuildConfig


object AdIds {
    private const val admobBannerId = "ca-app-pub-7135106726582637/9114434150"
    private const val admobInterstitialIdHome = "ca-app-pub-7135106726582637/2549025805"
    private const val appOpenIdHome = "ca-app-pub-7135106726582637/2357454115"
    private const val admobInterstitialIdSplash = ""
    private const val admobNativeId = "ca-app-pub-7135106726582637/4522717257"

    private const val admobBannerIdTest = "ca-app-pub-3940256099942544/6300978111"
    private const val admobInterstitialIdTest = "ca-app-pub-3940256099942544/1033173712"
    private const val appOpenIdTest = "ca-app-pub-3940256099942544/3419835294"
    private const val admobNativeIdTest = "ca-app-pub-3940256099942544/2247696110"

    private const val fbBannerId = ""
    private const val fbInterstitialId = ""
    private const val fbNativeId = ""

    private const val fbBannerIdTest = "IMG_16_9_APP_INSTALL#359277049370456_359278532703641"
    private const val fbInterstitialIdTest = "IMG_16_9_APP_INSTALL#359277049370456_359278672703627"
    private const val fbNativeIdTest = ""


    fun getAdmobBannerId(): String? {
        return if (BuildConfig.DEBUG) {
            admobBannerIdTest
        } else {
            admobBannerId
        }
    }

    fun getAppOpenId(): String? {
        return if (BuildConfig.DEBUG) {
            appOpenIdTest
        } else {
            appOpenIdHome
        }
    }

    fun admobInterstitialIdHome(): String? {
        return if (BuildConfig.DEBUG) {
            admobInterstitialIdTest
        } else {
            admobInterstitialIdHome
        }
    }

    fun admobInterstitialIdSplash(): String? {
        return if (BuildConfig.DEBUG) {
            admobInterstitialIdTest
        } else {
            admobInterstitialIdSplash
        }
    }


    fun getAdmobNativeId(): String? {
        return if (BuildConfig.DEBUG) {
            admobNativeIdTest
        } else {
            return admobNativeId
        }
    }


    fun getFbBannerId(): String? {
        return if (BuildConfig.DEBUG) {
            fbBannerIdTest
        } else {
            fbBannerId
        }
    }

    fun getFbInterstitialId(): String? {
        return if (BuildConfig.DEBUG) {
            fbInterstitialIdTest
        } else {
            fbInterstitialId
        }
    }


    fun getFbNativeId(): String? {
        return if (BuildConfig.DEBUG) {
            fbNativeIdTest
        } else {
            fbNativeId
        }
    }


}