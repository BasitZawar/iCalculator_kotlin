package com.cyber.tarzan.calculator.ui.splash

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cyber.tarzan.calculator.R
import com.cyber.tarzan.calculator.admob.AdIds.admobInterstitialIdHome
import com.cyber.tarzan.calculator.admob.AdIds.getAdmobNativeId
import com.cyber.tarzan.calculator.admob.AdMobInterstitial
import com.cyber.tarzan.calculator.admob.InterstitialClosedListener
import com.cyber.tarzan.calculator.admob.InterstitialClosedListenerImplementer
import com.cyber.tarzan.calculator.databinding.ActivityLauncherBinding
import com.cyber.tarzan.calculator.ui.main.MainActivity
import com.cyber.tarzan.calculator.util.InfoUtil
import com.cyber.tarzan.calculator.util.VerificationCheck.playStoreAppVerification
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding
    private val MY_REQUEST_CODE = 500


    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    private var nativeAd: NativeAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvPrivacyPolicy.setOnClickListener {
            privacyPolicy()
            InfoUtil(this).privacy()

        }

// inAppUpdate
        checkForAppUpdate()

        if (playStoreAppVerification(this)) {

            MobileAds.initialize(this)

            AdMobInterstitial.loadInterstitialAd(
                this@LauncherActivity,
                admobInterstitialIdHome()
            )
//                Objects.requireNonNull(instance)
//                    ?.showAdMobBanner(this@SplashScreen, this, findViewById(R.id.bannerLayout))

        }


        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.isVisible = false
            binding.btnGetStarted.isVisible = true
        }, 3000)
        binding.btnGetStarted.setOnClickListener {
            showInterstitialAd()

        }

//        refreshAd()
    }


    override fun onResume() {
        super.onResume()
        checkNewAppVersionState()
    }

    private fun showInterstitialAd() {
        if (AdMobInterstitial.isAlreadyLoaded) {
            AdMobInterstitial.showInterstitial(this@LauncherActivity, false)
            InterstitialClosedListenerImplementer.setOnInterstitialClosedMaster(object :
                InterstitialClosedListener {
                override fun onInterstitialClosed() {
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
//                    Log.d("TAG", "onInterstitialClosed: move to next screen")
                }

                override fun onInterstitialFailedToShow() {
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
//                    Log.d("TAG", "onInterstitialFailedToShow: move to next screen")
                }
            })
        } else {
            startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
            Log.d("TAG", "onCreate: move to next screen")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (nativeAd != null) {
            nativeAd?.destroy()
        }

        unregisterInstallStateUpdListener()
    }

    // Ads methods
    private fun refreshAd() {
        val builder = AdLoader.Builder(
            this,
            getAdmobNativeId()
        )
        builder.forNativeAd(NativeAd.OnNativeAdLoadedListener { nativeAd ->

            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed = isDestroyed()
            }
            if (isDestroyed || isFinishing || isChangingConfigurations) {
                nativeAd.destroy()
                return@OnNativeAdLoadedListener
            }
            if (this@LauncherActivity.nativeAd != null) {
                this@LauncherActivity.nativeAd!!.destroy()
            }
            this@LauncherActivity.nativeAd = nativeAd
            binding.frameLayout.visibility = View.VISIBLE
            val adView = layoutInflater.inflate(R.layout.native_medium, null) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            binding.frameLayout.removeAllViews()
            binding.frameLayout.addView(adView)
        })
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
        })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView!!.setMediaContent(nativeAd.mediaContent)
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            (adView.starRatingView as RatingBar).visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            (adView.advertiserView as TextView).visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
        val vc = nativeAd.mediaContent?.videoController
        if (vc != null) {
            if (vc.hasVideoContent()) {
                vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
            }
        }

    }

    private fun checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

        installStateUpdatedListener =
            InstallStateUpdatedListener { installState ->
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    popupSnackbarForCompleteUpdateAndUnregister()
            }

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdateManager!!.registerListener(installStateUpdatedListener!!)
                    startAppUpdateFlexible(appUpdateInfo)
                }
            }
        }
    }

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                this,
                MY_REQUEST_CODE
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
            unregisterInstallStateUpdListener()
        }
    }

    private fun popupSnackbarForCompleteUpdateAndUnregister() {
        val parentLayout: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(
            parentLayout,
            getString(R.string.update_downloaded),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(
            R.string.restart
        ) { appUpdateManager!!.completeUpdate() }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
        unregisterInstallStateUpdListener()
    }

    private fun checkNewAppVersionState() {
        appUpdateManager
            ?.appUpdateInfo
            ?.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdateAndUnregister()
                }
            }
    }

    private fun unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null) appUpdateManager!!.unregisterListener(
            installStateUpdatedListener!!
        )
    }

    private fun privacyPolicy() {
        val url =
            "https://www.niamtechnologies.com/privacy"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }
}