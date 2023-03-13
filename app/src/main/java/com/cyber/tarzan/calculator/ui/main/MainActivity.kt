package com.cyber.tarzan.calculator.ui.main

import android.animation.*
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cyber.tarzan.calculator.R
import com.cyber.tarzan.calculator.admob.*
import com.cyber.tarzan.calculator.databinding.ActivityMainBinding
import com.cyber.tarzan.calculator.history.History
import com.cyber.tarzan.calculator.ui.history.HistoryActivity
import com.cyber.tarzan.calculator.ui.main.helper.*
import com.cyber.tarzan.calculator.ui.main.viewmodel.MainViewModel
import com.cyber.tarzan.calculator.ui.settings.Setting_Activity
import com.cyber.tarzan.calculator.ui.view.CalculatorEditText
import com.cyber.tarzan.calculator.util.AngleType
import com.cyber.tarzan.calculator.util.NumberSeparator
import com.cyber.tarzan.calculator.util.PrefUtil
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.button.MaterialButton
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.type.Color
import dagger.hilt.android.AndroidEntryPoint
import petrov.kristiyan.colorpicker.BuildConfig


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var nativeAd: NativeAd? = null
    private var mAdView: AdView? = null
    private val viewModel: MainViewModel by viewModels()
    private var mCurrentAnimator: Animator? = null
    var textColorMainActivity: Int? = null
    var constraintlayout1: Int? = null
    var prefUtil: PrefUtil? = null
    private val reviewManager: ReviewManager? = null
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)
//        refreshAd()

        val reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                setInAppReview(reviewInfo)

            } else {
                Log.e("application2", "onCreate: " + task.result)
            }
        }

//        mainScreenBannerLayout = findViewById(R.id.mainScreenBannerLayout)
//        setContentView(R.layout.activity_main)
//        setupActionBar(binding.toolbar)

// clear FLAG_TRANSLUCENT_STATUS flag:
        val window: Window = this.window
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setupView()
        setupObservers()
        setClickListener()

//        adView = findViewById(R.id.mainScreenBannerLayout)

//Banner ad
//        if (VerificationCheck.playStoreAppVerification(this@MainActivity)) {
        if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
            AdsManager.instance?.showAdMobBanner(
                this@MainActivity,
                this@MainActivity,
                binding.mainScreenBannerLayout!!
            )
        }
        Log.d(TAG, "onCreate: Banner add shown")

//        }

        prefUtil = PrefUtil(applicationContext)

        if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
            binding.percent?.setOnClickListener(buttonClick)
            binding.factorial?.setOnClickListener(buttonClick)
            binding.divide?.setOnClickListener(buttonClick)
            binding.decimal?.setOnClickListener(buttonClick)
            binding.seven?.setOnClickListener(buttonClick)
            binding.eight?.setOnClickListener(buttonClick)
            binding.nine?.setOnClickListener(buttonClick)
            binding.multiply?.setOnClickListener(buttonClick)
            binding.four?.setOnClickListener(buttonClick)
            binding.five?.setOnClickListener(buttonClick)
            binding.six?.setOnClickListener(buttonClick)
            binding.minus?.setOnClickListener(buttonClick)
            binding.one?.setOnClickListener(buttonClick)
            binding.two?.setOnClickListener(buttonClick)
            binding.three?.setOnClickListener(buttonClick)
            binding.plus?.setOnClickListener(buttonClick)
            binding.delete?.setOnClickListener {
                it.isHapticFeedbackEnabled = true
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                val expression = removeNumberSeparator(getExpression())
                if (expression.isEmpty()) {
                    return@setOnClickListener
                }
                val newExpression = if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
                    addNumberSeparator(
                        expression = handleDelete(expression),
                        isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
                    )
                } else {
                    handleDelete(expression)
                }
                setExpression(newExpression)
            }
            binding.AC?.setOnClickListener {
                binding.expression?.text = null
                binding.result?.text = null
            }
            binding.btnBack?.setOnClickListener {
                if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                } else {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
            binding.equal?.setOnClickListener {
                it.isHapticFeedbackEnabled = true
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                val expression = removeNumberSeparator(getExpression())
                val result = getResult()
                if (expression.isNotEmpty()) {
                    if (result.isEmpty() || !removeNumberSeparator(result).isNumber()) {
                        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
                        getResultEditText()!!.setTextColor(getResultTextColor(true))
                        val errorStringId = viewModel.error.value ?: R.string.invalid
                        if (errorStringId == -1) {
                            setResult("")
                        } else {
                            setResult(getString(errorStringId))
                            getResultEditText()!!.startAnimation(shake)
                            isHistoryAvailable = false
                        }

                    } else {
                        isHistoryAvailable = true
                        val balancedExpression = viewModel.getCalculatedExpression()
                        val history = History(
                            expression = balancedExpression,
                            result = result,
                            date = System.currentTimeMillis()
                        )
                        viewModel.insertHistory(history)
                        viewModel.isPrevResult = true
                        setExpressionAfterEqual(result)
                    }
                }
            }
        }

        // delete button
        binding.delete?.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val expression = removeNumberSeparator(getExpression())
            if (expression.isEmpty()) {
                return@setOnClickListener
            }
            val newExpression = if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
                addNumberSeparator(
                    expression = handleDelete(expression),
                    isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
                )
            } else {
                handleDelete(expression)
            }
            setExpression(newExpression)
        }

        //history icon
        binding.historyIcon?.setOnClickListener {
//            if (isHistoryAvailable) {
            showInterstitialAdHistory()
//            } else {
//                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
//
////                Toast.makeText(this, "no history found", Toast.LENGTH_SHORT).show()
//            }
        }

        // setting icon
        binding.setting?.setOnClickListener {
//            showInterstitialAdSetting()
            settingDialog()
//            startActivity(Intent(this@MainActivity, Setting_Activity::class.java))
        }

        // scientific calculator icon to change orientation
        binding.scientificCalIcon?.setOnClickListener {
            if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                islandscape = true
            } else {
                islandscape = false
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    //today
    private fun changeTextColor() {
        binding.one!!.setTextColor(textColorMainActivity!!)
        binding.two!!.setTextColor(textColorMainActivity!!)
        binding.three!!.setTextColor(textColorMainActivity!!)
        binding.four!!.setTextColor(textColorMainActivity!!)
        binding.five!!.setTextColor(textColorMainActivity!!)
        binding.six!!.setTextColor(textColorMainActivity!!)
        binding.seven!!.setTextColor(textColorMainActivity!!)
        binding.eight!!.setTextColor(textColorMainActivity!!)
        binding.nine!!.setTextColor(textColorMainActivity!!)
        binding.zero!!.setTextColor(textColorMainActivity!!)
        binding.decimal!!.setTextColor(textColorMainActivity!!)
        binding.result!!.setTextColor(textColorMainActivity!!)
        binding.expression!!.setTextColor(textColorMainActivity!!)
    }

    private val buttonClick = View.OnClickListener {
        it.isHapticFeedbackEnabled = true
        it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        val text = (it as Button).text.toString()
        val expression = removeNumberSeparator(getExpression())
        var newExpression = handleClick(expression, text, viewModel.isPrevResult)
        viewModel.isPrevResult = false
        if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
            newExpression = addNumberSeparator(
                expression = newExpression,
                isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
            )
        }
        setExpression(newExpression)
    }

    private val textSizeChangeListener =
        CalculatorEditText.OnTextSizeChangeListener { textView, oldSize ->
            // Calculate the values needed to perform the scale and translation animations,
            // maintaining the same apparent baseline for the displayed text.
            val textScale = oldSize / textView.textSize
            val translationX = (1.0f - textScale) *
                    (textView.width / 2.0f - textView.paddingEnd)
            val translationY = (1.0f - textScale) *
                    (textView.height / 2.0f - textView.paddingBottom)
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(
                ObjectAnimator.ofFloat(textView, View.SCALE_X, textScale, 1.0f),
                ObjectAnimator.ofFloat(textView, View.SCALE_Y, textScale, 1.0f),
                ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, translationX, 0.0f),
                ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, translationY, 0.0f)
            )
            animatorSet.duration =
                resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            animatorSet.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.start()
        }

    private val expressionChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            setResult("")
            getResultEditText()!!.setTextColor(getResultTextColor())
            if (!removeNumberSeparator(s.toString()).isNumber()) {
                viewModel.calculateExpression(s.toString())
            }
        }
    }

    private fun setClickListener() {
        binding.percent?.setOnClickListener(buttonClick)
        binding.AC?.setOnClickListener {
            binding.expression?.text = null
            binding.result?.text = null
        }
        binding.factorial?.setOnClickListener(buttonClick)
        //second row
        binding.seven?.setOnClickListener(buttonClick)
        binding.eight?.setOnClickListener(buttonClick)
        binding.nine?.setOnClickListener(buttonClick)
        binding.divide?.setOnClickListener(buttonClick)
        //third row
        binding.four?.setOnClickListener(buttonClick)
        binding?.five?.setOnClickListener(buttonClick)
        binding?.six?.setOnClickListener(buttonClick)
        binding?.multiply?.setOnClickListener(buttonClick)
        //fourth row
        binding?.one?.setOnClickListener(buttonClick)
        binding?.two?.setOnClickListener(buttonClick)
        binding?.three?.setOnClickListener(buttonClick)
        binding?.plus?.setOnClickListener(buttonClick)
        //fifth row
        binding?.decimal?.setOnClickListener(buttonClick)
        binding?.zero?.setOnClickListener(buttonClick)
        binding?.minus?.setOnClickListener(buttonClick)

        //scientific Pad
        //first row
        binding.sin?.setOnClickListener(buttonClick)
        binding.cos?.setOnClickListener(buttonClick)
        binding.tan?.setOnClickListener(buttonClick)
        binding.openBracket?.setOnClickListener(buttonClick)
//            //second row
        binding.closeBracket?.setOnClickListener(buttonClick)
//            //third row
        binding.exponential?.setOnClickListener(buttonClick)
        binding.log?.setOnClickListener(buttonClick)
        binding.naturalLog?.setOnClickListener(buttonClick)
        binding.power?.setOnClickListener(buttonClick)
//            //fourth row
        binding.factorial?.setOnClickListener(buttonClick)
        binding.squareRoot?.setOnClickListener(buttonClick)
        binding.cubeRoot?.setOnClickListener(buttonClick)
        binding.pi?.setOnClickListener(buttonClick)

        //delete onClick
        binding.deleteLandscape?.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val expression = removeNumberSeparator(getExpression())
            if (expression.isEmpty()) {
                return@setOnClickListener
            }
            val newExpression = if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
                addNumberSeparator(
                    expression = handleDelete(expression),
                    isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
                )
            } else {
                handleDelete(expression)
            }
            setExpression(newExpression)
        }

        //delete long click
        binding?.deleteLandscape?.setOnLongClickListener {
            if (getExpression().isNotEmpty()) {
                animateClear()
            }
            true
        }

        //equal onClick
        binding?.equal?.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val expression = removeNumberSeparator(getExpression())
            val result = getResult()
            if (expression.isNotEmpty()) {
                if (result.isEmpty() || !removeNumberSeparator(result).isNumber()) {
                    val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
                    getResultEditText()!!.setTextColor(getResultTextColor(true))
                    val errorStringId = viewModel.error.value ?: R.string.invalid
                    if (errorStringId == -1) {
                        setResult("")
                    } else {
                        setResult(getString(errorStringId))
                        getResultEditText()!!.startAnimation(shake)
                        isHistoryAvailable = false
                    }

                } else {
                    isHistoryAvailable = true
                    val balancedExpression = viewModel.getCalculatedExpression()
                    val history = History(
                        expression = balancedExpression,
                        result = result,
                        date = System.currentTimeMillis()
                    )
                    viewModel.insertHistory(history)
                    viewModel.isPrevResult = true
                    setExpressionAfterEqual(result)
                }
            }
        }
//today
        //memory store click
//        binding.memoryStore?.setOnClickListener {
//            it.isHapticFeedbackEnabled = true
//            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
//            val result = removeNumberSeparator(getResult())
//            if (result.isNumber()) {
//                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
//                viewModel.setMemory(result)
//            }
//        }
//
//        //memory restore click
//        binding.scientificPad?.memoryRestore?.setOnClickListener {
//            it.isHapticFeedbackEnabled = true
//            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
//            val memory = viewModel.getMemory()
//            val expression = removeNumberSeparator(getExpression())
//            var newExpression = handleConstantClick(expression, memory, viewModel.isPrevResult)
//            viewModel.isPrevResult = false
//            if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
//                newExpression = addNumberSeparator(
//                    expression = newExpression,
//                    isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
//                )
//            }
//            setExpression(newExpression)
//        }
//
//        binding.scientificPad?.memoryAdd?.setOnClickListener {
//            it.isHapticFeedbackEnabled = true
//            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
//            val memory = viewModel.getMemory()
//            val result = removeNumberSeparator(getResult())
//            if (result.isNumber() && memory.isNumber()) {
//                val newMemory = memory.toDouble() + result.toDouble()
//                viewModel.setMemory(newMemory.toString())
//            }
//        }
//
//        binding.scientificPad?.memorySub?.setOnClickListener {
//            it.isHapticFeedbackEnabled = true
//            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
//            val memory = viewModel.getMemory()
//            val result = removeNumberSeparator(getResult())
//            if (result.isNumber() && memory.isNumber()) {
//                val newMemory = memory.toDouble() - result.toDouble()
//                viewModel.setMemory(newMemory.toString())
//            }
//        }

//        binding.calculatorPadViewPager?.addScientificPadStateChangeListener {
//            binding.scientificPad.arrow.animate().rotationBy(180F).setDuration(300).start()
//        }

    }


    private fun setupObservers() {
        viewModel.result.observe(this) {
            setResult(it)
        }
    }

    private fun setupView() {
        viewModel.updateLaunchStatistics()
        binding.expression?.setOnTextSizeChangeListener(textSizeChangeListener)
        binding.expression?.addTextChangedListener(expressionChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            it.findItem(R.id.angleType).title = viewModel.getAngleType()
        }
        return super.onPrepareOptionsMenu(menu)
    }


    override fun onBackPressed() {
        appExitAlertDialog()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (nativeAd != null) {
            nativeAd?.destroy()
        }
    }

    private fun appExitAlertDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup: ViewGroup = this@MainActivity.findViewById(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView: View = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.custom_promp_exit_popup, viewGroup, false)


        val builder = AlertDialog.Builder(this@MainActivity)
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

            finishAffinity()
            alertDialog.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
//                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.history -> {
//                startActivity(Intent(this, HistoryActivity::class.java))
            }
            R.id.about -> {
//                startActivity(Intent(this, AboutActivity::class.java))
            }
            R.id.share -> {
                val sharedEquation = getShareEquation()
                if (sharedEquation.isNotEmpty()) {
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
                } else {
                    Toast.makeText(this, getString(R.string.share_error), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.tutorial -> {
                showTutorial()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTutorial() {
//        close side panel before starting tutorial
//        if (binding.calculatorPadViewPager?.currentItem == 1) {
//            binding.calculatorPadViewPager?.currentItem = 0
//        }
        val tapTargetSequence = TapTargetSequence(this)
        val delete = TapTarget
            .forView(
                binding?.deleteLandscape,
                getString(R.string.delete_button),
                getString(R.string.delete_button_desc)
            )
            .outerCircleColor(R.color.primary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(false)
            .titleTextColor(R.color.white)
            .descriptionTextColor(R.color.white)
            .descriptionTextSize(18)
            .cancelable(true)
//        val angle: TapTarget = TapTarget
//            .forToolbarMenuItem(
//                binding.toolbar,
//                R.id.angleType,
//                getString(R.string.angle_button),
//                getString(R.string.angle_button_desc)
//            )
//            .outerCircleColor(R.color.primary)
//            .outerCircleAlpha(1f)
//            .targetCircleColor(R.color.white)
//            .titleTextSize(28)
//            .tintTarget(true)
//            .titleTextColor(R.color.white)
//            .descriptionTextColor(R.color.white)
//            .descriptionTextSize(18)
//            .cancelable(true)
//        val options: TapTarget = TapTarget
//            .forToolbarOverflow(
//                binding.toolbar,
//                getString(R.string.options_menu),
//                getString(R.string.options_menu_desc)
//            )
//            .outerCircleColor(R.color.primary)
//            .outerCircleAlpha(1f)
//            .targetCircleColor(R.color.white)
//            .titleTextSize(28)
//            .tintTarget(true)
//            .titleTextColor(R.color.white)
//            .descriptionTextColor(R.color.white)
//            .descriptionTextSize(18)
//            .cancelable(true)
//            .id(56)
//        val share: TapTarget = TapTarget
//            .forToolbarMenuItem(
//                binding.toolbar,
//                R.id.share,
//                getString(R.string.share_button),
//                getString(R.string.share_button_desc)
//            )
//            .outerCircleColor(R.color.primary)
//            .outerCircleAlpha(1f)
//            .targetCircleColor(R.color.white)
//            .titleTextSize(28)
//            .tintTarget(true)
//            .titleTextColor(R.color.white)
//            .descriptionTextColor(R.color.white)
//            .descriptionTextSize(18)
//            .cancelable(true)
//        val ms = TapTarget.forView(
//            binding.scientificPad?.memoryStore,
//            getString(R.string.memory_store), getString(R.string.memory_store_desc)
//        )
            .outerCircleColor(R.color.numPadPrimary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(false)
            .titleTextColor(R.color.textPrimary)
            .descriptionTextColor(R.color.textPrimary)
            .descriptionTextSize(18)
            .cancelable(true)
//        val mr = TapTarget.forView(
//            binding.scientificPad?.memoryRestore,
//            getString(R.string.memory_restore), getString(R.string.memory_restore_desc)
//        )
            .outerCircleColor(R.color.numPadPrimary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(false)
            .titleTextColor(R.color.textPrimary)
            .descriptionTextColor(R.color.textPrimary)
            .descriptionTextSize(18)
            .cancelable(true)

//        tapTargetSequence.targets(delete, angle, share, options, ms, mr)

//        tapTargetSequence.listener(object : TapTargetSequence.Listener {
//            override fun onSequenceFinish() {
////                binding.calculatorPadViewPager?.currentItem = 0
//            }
//
//            override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
//                if (lastTarget.id() == 56) {
////                    binding.calculatorPadViewPager?.currentItem = 1
//                }
//            }
//
//            override fun onSequenceCanceled(lastTarget: TapTarget) {}
//        })
//        tapTargetSequence.continueOnCancel(true).start()
    }

    private fun getShareEquation(): String {
        if (getExpression().isNotEmpty()) {
            val result = getResult()
            return if (result == "" || !removeNumberSeparator(result).isNumber()) {
                ""
            } else {
                val expression = viewModel.getCalculatedExpression()
                "$expression = $result"
            }
        }
        return ""
    }


    fun changeAngleType(menuItem: MenuItem) {
        val text = menuItem.title.toString()
        if (text == AngleType.DEG.name) {
            menuItem.title = AngleType.RAD.name
            viewModel.changeAngleType(AngleType.RAD)
        } else {
            menuItem.title = AngleType.DEG.name
            viewModel.changeAngleType(AngleType.DEG)
        }
        val currentExpression = getExpression()
        setExpression(currentExpression)
    }

    private fun getResultTextColor(isError: Boolean = false): Int {
        val typedValue = TypedValue()
        val typedArray = if (isError) {
            obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorError))
        } else {
            obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.textDisable))
        }
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    private fun animateClear() {
//        with(binding) {
//            val cx = clearView!!.right
//            val cy = clearView.bottom
//            val l = clearView.height
//            val b = clearView.width
//            val finalRadius = sqrt((l * l + b * b).toDouble()).toInt()
//            val anim = ViewAnimationUtils
//                .createCircularReveal(clearView, cx, cy, 0f, finalRadius.toFloat())
//            clearView.visibility = View.VISIBLE
//            anim.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
//            anim.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator) {
//                    setExpression("")
//                    setResult("")
//                    getResultEditText().setTextColor(getResultTextColor())
//                    clearView.visibility = View.INVISIBLE
//                    mCurrentAnimator = null
//                }
//            })
//            mCurrentAnimator = anim
//            anim.start()
//        }
        setExpression("")
        setResult("")
        getResultEditText()!!.setTextColor(getResultTextColor())
    }

    private fun setExpressionAfterEqual(answer: String) {
        // Calculate the values needed to perform the scale and translation animations,
        // accounting for how the scale will affect the final position of the text.
        val expression = getExpressionEditText()
        val result = getResultEditText()
        val resultScale = expression!!.getVariableTextSize(answer) / result!!.textSize
        val resultTranslationX = (1.0f - resultScale) * (result.width / 2.0f - result.paddingEnd)
        val resultTranslationY = (1.0f - resultScale) *
                (result!!.height / 2.0f - result.paddingBottom) +
                (expression.bottom - result.bottom) +
                (result.paddingBottom - expression.paddingBottom)
        val formulaTranslationY = -expression.bottom.toFloat()

        // Use a value animator to fade to the final text color over the course of the animation.
        val resultTextColor: Int = result.currentTextColor
        val formulaTextColor: Int = expression.currentTextColor
        val textColorAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), resultTextColor, formulaTextColor)
        textColorAnimator.addUpdateListener { valueAnimator ->
            result.setTextColor(valueAnimator.animatedValue as Int)
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            textColorAnimator,
            ObjectAnimator.ofFloat(result, View.SCALE_X, resultScale),
            ObjectAnimator.ofFloat(result, View.SCALE_Y, resultScale),
            ObjectAnimator.ofFloat(result, View.TRANSLATION_X, resultTranslationX),
            ObjectAnimator.ofFloat(result, View.TRANSLATION_Y, resultTranslationY),
            ObjectAnimator.ofFloat(expression, View.TRANSLATION_Y, formulaTranslationY)
        )
        animatorSet.duration =
            resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                // Reset all of the values modified during the animation.
                if (result != null) {
                    result.setTextColor(resultTextColor)
                }
                if (result != null) {
                    result.scaleX = 1.0f
                }
                result!!.scaleY = 1.0f
                result.translationX = 0.0f
                result.translationY = 0.0f
                expression!!.translationY = 0.0f

                // Finally update the formula to use the current result.
                expression.setText(answer)
                result.setText("")
                mCurrentAnimator = null
            }
        })
        mCurrentAnimator = animatorSet
        animatorSet.start()
    }

    private fun getExpressionEditText(): CalculatorEditText? {
        return binding.expression
    }

    private fun getResultEditText(): CalculatorEditText? {
        return binding.result
    }

    private fun setExpression(expression: String) {
        getExpressionEditText()?.setText(expression)
    }

    private fun setResult(result: String) {
        getResultEditText()?.setText(result)
    }

    private fun getExpression(): String {
        return binding.expression?.text.toString().trim()
    }

    private fun getResult(): String {
        return binding.result?.text.toString().trim()
    }

    override fun onResume() {
        super.onResume()

//        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()

    }

    override fun onStart() {
        super.onStart()
//        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show()


//text color
        textColorMainActivity = prefUtil!!.getInt("textColor", 0)
        if (prefUtil!!.getInt("textColor", 0) == 0) {
//            Toast.makeText(applicationContext, "preference are null", Toast.LENGTH_SHORT).show()
        } else {

            changeTextColor()

//            Toast.makeText(applicationContext, "preference are not null", Toast.LENGTH_SHORT).show()
        }


        // background color
        constraintlayout1 = prefUtil!!.getInt("BackgroundColor", 0)
        if (prefUtil!!.getInt("BackgroundColor", 0) == 0) {
        } else {
            binding.activityMain!!.setBackgroundColor(constraintlayout1!!)
            binding.expression!!.setBackgroundColor(constraintlayout1!!)
            binding.result!!.setBackgroundColor(constraintlayout1!!)
            if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
                binding.portraitNumPad!!.setBackgroundColor(constraintlayout1!!)
            } else {
                binding.layCalIcon?.setBackgroundColor(constraintlayout1!!)
                binding.resultPadLand!!.setBackgroundColor(constraintlayout1!!)
            }
        }

        //=================================
        var savedExpression = viewModel.getSavedExpression()
        if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
            savedExpression = addNumberSeparator(
                expression = savedExpression,
                isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
            )
        }
        setExpression(savedExpression)
//        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        mCurrentAnimator?.end()
        val currentExpression = removeNumberSeparator(getExpression())
        viewModel.saveExpression(currentExpression)
    }

//    override fun getViewBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)

    //show admob interstitial ad
    private fun showInterstitialAdHistory() {
        if (AdMobInterstitial.isAlreadyLoaded) {
            AdMobInterstitial.showInterstitial(this@MainActivity, false)
            InterstitialClosedListenerImplementer.setOnInterstitialClosedMaster(object :
                InterstitialClosedListener {
                override fun onInterstitialClosed() {
                    startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                }

                override fun onInterstitialFailedToShow() {
                    startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                }
            })
        } else {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            Log.d("TAG", "onCreate: move to next screen")
        }
    }

    private fun showInterstitialAdSetting() {
        if (AdMobInterstitial.isAlreadyLoaded) {
            AdMobInterstitial.showInterstitial(this@MainActivity, false)
            InterstitialClosedListenerImplementer.setOnInterstitialClosedMaster(object :
                InterstitialClosedListener {
                override fun onInterstitialClosed() {
                    if (binding.historyIcon!!.isClickable)
                        startActivity(Intent(this@MainActivity, Setting_Activity::class.java))
                }

                override fun onInterstitialFailedToShow() {
                    startActivity(Intent(this@MainActivity, Setting_Activity::class.java))
                }
            })
        } else {
            startActivity(Intent(this@MainActivity, Setting_Activity::class.java))
            Log.d("TAG", "onCreate: move to next screen")
        }
    }

    companion object {
        var isHistoryAvailable: Boolean = false
        var islandscape: Boolean = false
    }

    fun initializeAds() {
//        adContainerView = binding.adsview
//        adContainerView.setVisibility(View.VISIBLE);
        mAdView = AdView(this)
        this.mAdView!!.adUnitId = AdIds.getAdmobBannerId()
//        adContainerView.addView(mAdView);
        banner();
    }

    fun banner() {

        try {
            val adRequest: AdRequest = AdRequest.Builder().build()
            val adSize = getAdSize()
            mAdView!!.setAdSize(adSize!!)
            mAdView!!.loadAd(adRequest)
            mAdView!!.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Code to be executed when an ad request fails.
//                    adContainerView.visibility = View.INVISIBLE
                }

                override fun onAdOpened() {
                }

                override fun onAdClicked() {
                }

                override fun onAdClosed() {
                }
            }
        } catch (ex: java.lang.Exception) {

        }

    }

    private fun getAdSize(): AdSize? {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

    // When User cilcks on dialog button, call this method
    private fun settingDialog() {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup: ViewGroup = this@MainActivity.findViewById(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView: View = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.custom_setting_dialog, viewGroup, false)


        val builder = AlertDialog.Builder(this@MainActivity)
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.GREEN_FIELD_NUMBER))
        alertDialog.show()
        //background color
//        val linear_settingDialog =
//            dialogView.findViewById<ConstraintLayout>(R.id.linear_settingDialog)
//        linear_settingDialog.setBackgroundColor(constraintlayout1!!)
        //text color
        if (prefUtil!!.getInt("textColor", 0) != 0) {
            val tv_more = dialogView.findViewById<TextView>(R.id.tv_More)
            tv_more.setTextColor(textColorMainActivity!!)
            val tv_setting = dialogView.findViewById<TextView>(R.id.tv_settings)
            tv_setting.setTextColor(textColorMainActivity!!)
            val tv_rate = dialogView.findViewById<TextView>(R.id.tv_RateApp)
            tv_rate.setTextColor(textColorMainActivity!!)
            val tv_share = dialogView.findViewById<TextView>(R.id.tv_ShareApp)
            tv_share.setTextColor(textColorMainActivity!!)
            val tv_feedback = dialogView.findViewById<TextView>(R.id.tv_MoreApp)
            tv_feedback.setTextColor(textColorMainActivity!!)
        } else {
        }
        //
        val btn_more = dialogView.findViewById<TextView>(R.id.tv_More)
        btn_more.setOnClickListener {
            showInterstitialAdSetting()
            alertDialog.dismiss()
        }
        val btn_rate = dialogView.findViewById<TextView>(R.id.tv_RateApp)
        btn_rate.setOnClickListener {
            rateApp()
        }
        val btn_share = dialogView.findViewById<TextView>(R.id.tv_ShareApp)
        btn_share.setOnClickListener {
            shareApp()
        }
        val btn_moreApps = dialogView.findViewById<TextView>(R.id.tv_MoreApp)
        btn_moreApps.setOnClickListener {
//            feedback()
            moreApps()
        }
    }

    fun moreApps() {
        try {
            val url =
                "https://play.google.com/store/apps/dev?id=6148212530509697995&hl=en-IN" +
                        "zQxMjk5NzA0NTI0MjQQCBgDEhkKEzQ4MDEyMzQxMjk5NzA0NTI0MjQQCBgDGAA%3D:S:ANO1ljJ5IaI&gsr=" +
                        "CjuKAzgKGQoTNDgwMTIzNDEyOTk3MDQ1MjQyNBAIGAMSGQoTNDgwMTIzNDEyOTk3MDQ1MjQyNBAIGAMYAA%3D%3D:S:ANO1ljJ56NU"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: java.lang.Exception) {
            // Catch Exception here
        }
    }

    //Feedback
    private fun feedback() {
        val feedbackEmail = Intent(Intent.ACTION_SEND)
        feedbackEmail.type = "text/email"
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("basitzawar@gmail.com"))
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"))
    }

    // rate app
    private fun rateApp() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    // in app review
    private fun setInAppReview(reviewInfo: ReviewInfo) {
        val flow = reviewManager?.launchReviewFlow(this, reviewInfo)
        flow?.addOnCompleteListener { }
    }

    //share app
    fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            val mBuildConfig: BuildConfig? = null
            shareMessage =
                """
            ${shareMessage}https://play.google.com/store/apps/details?id=com.calculator.ct}
            """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }
}