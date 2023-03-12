package com.cyber.tarzan.calculator.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cyber.tarzan.calculator.R
import com.cyber.tarzan.calculator.admob.AdIds
import com.cyber.tarzan.calculator.admob.AdMobInterstitial
import com.cyber.tarzan.calculator.admob.InterstitialClosedListener
import com.cyber.tarzan.calculator.admob.InterstitialClosedListenerImplementer
import com.cyber.tarzan.calculator.databinding.ActivitySettingBinding
import com.cyber.tarzan.calculator.ui.history.HistoryActivity
import com.cyber.tarzan.calculator.ui.main.MainActivity
import com.cyber.tarzan.calculator.util.PrefUtil
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import petrov.kristiyan.colorpicker.ColorPicker

class Setting_Activity : AppCompatActivity() {

    private var nativeAd: NativeAd? = null


    private lateinit var binding: ActivitySettingBinding
    var prefUtil: PrefUtil? = null
    var colorPicker: ColorPicker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshAd()
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)


        //Banner ad

        //        if (VerificationCheck.playStoreAppVerification(this@MainActivity)) {
//        AdsManager.instance?.showAdMobBanner(
//            this@Setting_Activity,
//            this@Setting_Activity,
//            binding.settingBannerLayout!!
//        )
//
//        Log.d(ContentValues.TAG, "onCreate: Banner add shown")

//            }
//        }


//        settingBackgroundLayout = findViewById(R.id.settingsBackgroundLayout)
//        settingBackgroundLayout!!.setBackgroundColor(getColor(R.color.black))

        prefUtil = PrefUtil(applicationContext)
//=============================
        lastTextColor = prefUtil!!.getInt("textColor", 0)
        if (prefUtil!!.getInt("textColor", 0) == 0) {
        } else {
            colorPicker?.setColors(lastTextColor)
            changeTextColor()
        }

//=============================
//        lastBackgroundColor = getColor(R.color.black)
        lastBackgroundColor = prefUtil!!.getInt("BackgroundColor", 0)
        if (prefUtil!!.getInt("BackgroundColor", 0) == 0) {
        } else {
            colorPicker?.setColors(lastBackgroundColor)
            binding.settingsBackgroundLayout?.setBackgroundColor(lastBackgroundColor)
        }
//=============================
        // colors
        colorList = ArrayList<String>()
        colorList!!.add("#3aa8c1")
        colorList!!.add("#8bbe1b")
        colorList!!.add("#ffc3a0")
        colorList!!.add("#a29088")
        colorList!!.add("#1f2212")
        colorList!!.add("#8657c5")
        colorList!!.add("#caeb5e")
        colorList!!.add("#095e79")
        colorList!!.add("#33cc5a")
        colorList!!.add("#ba160c")
        colorList!!.add("#f498ad")
        colorList!!.add("#ba160c")
        colorList!!.add("#f498ad")
        colorList!!.add("#283747")
        colorList!!.add("#F1948A")
        colorList!!.add("#ABB2B9")
        colorList!!.add("#626567")
        colorList!!.add("#D0ECE7")
        colorList!!.add("#000000")
        colorList!!.add("#FFFBDA61")
        colorList!!.add("#FFFBAB7E")
        colorList!!.add("#FFF7CE68")
        colorList!!.add("#FFFFFB7D")
        colorList!!.add("#FFFF5ACD")
        colorList!!.add("#FFFFFFFF")


        binding.apply {

            arrowBack.setOnClickListener {
//                val intent = Intent(applicationContext, MainActivity::class.java)
//                startActivity(intent)
                finish()
            }

            layoutCard1.setOnClickListener {
//                showInterstitialAdHistory()
                val intent = Intent(applicationContext, HistoryActivity::class.java)
                startActivity(intent)
            }

            tvBackgroundColor.setOnClickListener {
                val colorPicker = ColorPicker(this@Setting_Activity)
                colorPicker.setColors(colorList)
                colorPicker.show()
                colorPicker.setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                    override fun onChooseColor(position: Int, color: Int) {
                        Log.d("TESTTAG", "Color $color")
                        // put code
                        if (color != lastTextColor) {
                            lastBackgroundColor = color
                            prefUtil!!.setInt("BackgroundColor", color)
                            binding.settingsBackgroundLayout!!.setBackgroundColor(
                                lastBackgroundColor
                            )
//                    colorPicker.dismissDialog()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "color should not be same as text color",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    override fun onCancel() {
                        // put code
                    }
                })

            }
            tvTextColor.setOnClickListener {
                val colorPicker = ColorPicker(this@Setting_Activity)
                colorPicker.setColors(colorList)
                colorPicker.show()

                colorPicker.setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                    override fun onChooseColor(position: Int, color: Int) {
                        Log.d("TESTTAG", "Color $color")
                        if (color!= lastBackgroundColor){
                            lastTextColor = color
                            prefUtil!!.setInt("textColor", color)
                            changeTextColor()
//                    colorPicker.dismissDialog()
                        }else{
                            Toast.makeText(
                                applicationContext,
                                "color should not be same as background color",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    override fun onCancel() {
                        // put code
                    }
                })
            }
//            tvRateApp.setOnClickListener {
//                rateApp()
//            }
//            tvShareApp.setOnClickListener {
//                shareApp()
//            }
//            tvFeedBack.setOnClickListener {
//                feedback()
//            }
            tvPrivacyPolicy.setOnClickListener {

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.cybertarzan.com/privacy")
                    )
                )
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

//    //share app
//    fun shareApp() {
//        try {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
//            var shareMessage = "\nLet me recommend you this application\n\n"
//            val mBuildConfig: BuildConfig? = null
//            shareMessage =
//                """
//            ${shareMessage}https://play.google.com/store/apps/details?id=com.calculator.ct}
//            """.trimIndent()
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
//            startActivity(Intent.createChooser(shareIntent, "choose one"))
//        } catch (e: Exception) {
//            //e.toString();
//        }
//    }

//    // rate app
//    private fun rateApp() {
//        val uri: Uri = Uri.parse("market://details?id=$packageName")
//        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
//        // To count with Play market backstack, After pressing back button,
//        // to taken back to our application, we need to add following flags to intent.
//        goToMarket.addFlags(
//            Intent.FLAG_ACTIVITY_NO_HISTORY or
//                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
//                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
//        )
//        try {
//            startActivity(goToMarket)
//        } catch (e: ActivityNotFoundException) {
//            startActivity(
//                Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
//                )
//            )
//        }
//    }

//    //Feedback
//    private fun feedback() {
//        val feedbackEmail = Intent(Intent.ACTION_SEND)
//        feedbackEmail.type = "text/email"
//        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("basitzawar@gmail.com"))
//        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
//        startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"))
//    }

    private fun changeTextColor() {
        binding.tvPrivacyPolicy.setTextColor(lastTextColor)
        binding.tvBackgroundColor.setTextColor(lastTextColor)
        binding.tvAppbar.setTextColor(lastTextColor)
        binding.tvHistoryOfCalculations.setTextColor(lastTextColor)
        binding.tvTextColor.setTextColor(lastTextColor)
    }

    companion object {
        lateinit var colorList: ArrayList<String>
        var lastTextColor = 0
        var lastBackgroundColor = 0

    }


    // natitve banner ad
    // Ads methods
    private fun refreshAd() {
        val builder = AdLoader.Builder(
            this,
            AdIds.getAdmobNativeId()
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
            if (this@Setting_Activity.nativeAd != null) {
                this@Setting_Activity.nativeAd!!.destroy()
            }
            this@Setting_Activity.nativeAd = nativeAd
            binding.frameLayout!!.visibility = View.VISIBLE
            val adView = layoutInflater.inflate(R.layout.native_medium, null) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            binding.frameLayout!!.removeAllViews()
            binding.adrelative!!.addView(adView)
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

    //show admob interstitial ad

    private fun showInterstitialAdHistory() {
        if (AdMobInterstitial.isAlreadyLoaded) {
            AdMobInterstitial.showInterstitial(this@Setting_Activity, false)
            InterstitialClosedListenerImplementer.setOnInterstitialClosedMaster(object :
                InterstitialClosedListener {
                override fun onInterstitialClosed() {
                    startActivity(Intent(this@Setting_Activity, HistoryActivity::class.java))
                }

                override fun onInterstitialFailedToShow() {
                    startActivity(Intent(this@Setting_Activity, HistoryActivity::class.java))
                }
            })
        } else {
            startActivity(Intent(this@Setting_Activity, HistoryActivity::class.java))
            Log.d("TAG", "onCreate: move to next screen")
        }

    }
}