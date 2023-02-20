package com.cyber.tarzan.calculator.ui.history

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyber.tarzan.calculator.R
import com.cyber.tarzan.calculator.admob.AdIds
import com.cyber.tarzan.calculator.admob.AdsManager
import com.cyber.tarzan.calculator.database.model.toDomain
import com.cyber.tarzan.calculator.databinding.ActivityHistoryBinding
import com.cyber.tarzan.calculator.history.HistoryAdapterItem
import com.cyber.tarzan.calculator.ui.base.BaseActivity
import com.cyber.tarzan.calculator.ui.history.adapter.HistoryAdapter
import com.cyber.tarzan.calculator.ui.history.viewmodel.HistoryViewModel
import com.cyber.tarzan.calculator.ui.main.helper.removeNumberSeparator
import com.cyber.tarzan.calculator.util.PrefUtil
import com.cyber.tarzan.calculator.util.visible
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.type.Color
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {
    private var nativeAd: NativeAd? = null


    private val viewModel by viewModels<HistoryViewModel>()
    private lateinit var arrowBack: ImageView
    private lateinit var deleteHistory: ImageView
    private var textViewAppbar: TextView? = null
    private var historyBackground: LinearLayout? = null
    private var historyBannerLayout: LinearLayout? = null
    private var noHistory: LinearLayout? = null
    private var rv: RecyclerView? = null
    var prefUtil: PrefUtil? = null
    var textViewColor: Int? = null
    var backgroundColor: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setSupportActionBar(binding.toolbar)

        // clear FLAG_TRANSLUCENT_STATUS flag:
        val window: Window = this.window
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)


        val historyModel: HistoryAdapterItem

//        refreshAd()

        // views initialization
        arrowBack = findViewById(R.id.btnArrowBack)
        deleteHistory = findViewById(R.id.btnDelete)
        textViewAppbar = findViewById(R.id.textViewAppbar)
        historyBackground = findViewById(R.id.historyBackground)
        noHistory = findViewById(R.id.noHistoryLayout)
        rv = findViewById(R.id.rv)
        historyBannerLayout = findViewById(R.id.historyBannerLayout)
        prefUtil = PrefUtil(applicationContext)
        textViewColor = prefUtil!!.getInt("textColor", 0)
        if (prefUtil!!.getInt("textColor", 0) != 0) {
            textViewAppbar!!.setTextColor(textViewColor!!)
        }


        backgroundColor = prefUtil!!.getInt("BackgroundColor", 0)
        if (prefUtil!!.getInt("BackgroundColor", 0) == 0) {
        } else {
            historyBackground!!.setBackgroundColor(backgroundColor!!)
            rv!!.setBackgroundColor(backgroundColor!!)
            noHistory!!.setBackgroundColor(backgroundColor!!)
        }

//Banner ad

        //        if (VerificationCheck.playStoreAppVerification(this@MainActivity)) {
        AdsManager.instance?.showAdMobBanner(
            this@HistoryActivity,
            this@HistoryActivity,
            historyBannerLayout!!
        )

        Log.d(ContentValues.TAG, "onCreate: Banner add shown")

//            }
//        }

        arrowBack.setOnClickListener {
            handleBackPress()
        }
        deleteHistory.setOnClickListener {
            if (viewModel.historyList.value.isNullOrEmpty()) {
                Toast.makeText(this, "No history found", Toast.LENGTH_SHORT).show()
            } else {
//                alertDialog()
                historyClearDialog()
            }
        }
        setupView()
        setupObservables()
    }

    private fun setupObservables() {
        viewModel.historyList.observe(this) { historyList ->
            if (historyList != null && historyList.isNotEmpty()) {
                binding.noHistoryLayout.visible(false)
                binding.rv.visible(true)

                val list = viewModel.transformHistory(historyList.map { it.toDomain() })
                val adapter = HistoryAdapter(list, object : HistoryAdapter.OnHistoryClickListener {
                    override fun onHistoryClick(history: HistoryAdapterItem) {
                        viewModel.saveExpression(removeNumberSeparator(history.expression))
                        finish()
                    }
                })
                binding.rv.adapter = adapter
            } else {
                binding.rv.visible(false)
                binding.noHistoryLayout.visible(true)

            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            101 -> {
                val position = item.groupId
                val history = (binding.rv.adapter as HistoryAdapter).getHistory(position)
                viewModel.deleteHistory(history.expression)
                true
            }
            102 -> {
                val position = item.groupId
                val history = (binding.rv.adapter as HistoryAdapter).getHistory(position)
                val sharedEquation = "${history.expression} = ${history.result}"
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Calculator Plus Expression")
                            putExtra(Intent.EXTRA_TEXT, sharedEquation)
                        },
                        getString(R.string.choose)
                    )
                )
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history_trash -> viewModel.clearHistory()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView() {
//        binding.toolbar.setNavigationOnClickListener { handleBackPress() }
    }

    override fun onBackPressed() {
        handleBackPress()
    }

    private fun handleBackPress() {
        finish()
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityHistoryBinding.inflate(inflater)

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("History will be cleared!")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->

        }
        builder.show()
    }


    private fun historyClearDialog() {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup: ViewGroup = this@HistoryActivity.findViewById(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView: View = LayoutInflater.from(this@HistoryActivity)
            .inflate(R.layout.history_clear_dialog, viewGroup, false)


        val builder = AlertDialog.Builder(this@HistoryActivity)
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        builder.setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.GREEN_FIELD_NUMBER))
        alertDialog.show()

        val tv_label_no = dialogView.findViewById<TextView>(R.id.tv_label_no)
        tv_label_no.setOnClickListener {
            alertDialog.dismiss()
        }

        val tv_label_yes = dialogView.findViewById<TextView>(R.id.tv_label_yes)
        tv_label_yes.setOnClickListener {
            viewModel.clearHistory()
            Toast.makeText(this, "History deleted successfully", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }
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
            if (this@HistoryActivity.nativeAd != null) {
                this@HistoryActivity.nativeAd!!.destroy()
            }
            this@HistoryActivity.nativeAd = nativeAd
            binding.historyBannerLayout!!.visibility = View.VISIBLE
            val adView = layoutInflater.inflate(R.layout.native_banner, null) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            binding.historyBannerLayout!!.removeAllViews()
            binding.historyBannerLayout!!.addView(adView)
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
}