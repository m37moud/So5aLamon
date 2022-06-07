package com.m37moud.mynewlang

import TranslateMessageIMPL
import android.animation.Animator
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.isVisible
import com.flipkart.chatheads.ui.ChatHeadContainer
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.m37moud.mynewlang.data.EncryptionMessageIMPL
import com.m37moud.mynewlang.ui.DialogFragment
import com.m37moud.mynewlang.ui.Translate
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.m37moud.mynewlang.util.Constants.Companion.AD_BANNER_ID
import com.m37moud.mynewlang.util.Constants.Companion.AD_InterstitialAd_ID
import com.m37moud.mynewlang.util.Constants.Companion.AD_REWARDEDED_ID
import com.m37moud.mynewlang.util.InvalidTextException
import com.m37moud.mynewlang.util.Logger
import com.sha.apphead.AppHead
import com.sha.apphead.BadgeView
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_floating_translate.view.*
import kotlinx.android.synthetic.main.layout_translate_app.view.*
import kotlinx.android.synthetic.main.layout_translate_app.view.okay_btn
import kotlinx.android.synthetic.main.layout_translate_app.view.translated_txt


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var animationStarted = false
    val STARTUP_DELAY = 300
    val ANIM_ITEM_DURATION = 1000
    val ITEM_DELAY = 300


    //ads
    private var mRewardedAd: RewardedAd? = null
    private var mAdIsLoading: Boolean = false
    private var adLoadCalled: Boolean = false //detect first load
    private var mAdIsFailed: Boolean = false //detect if ad is failed
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adView: AdView   //banner ads
    private var bannerAdShowed = false//banner ads
    private var numOfShow = 0


    private var isViewCollapsed = false

    //end of ads refrence

    //cliboard

    private val mClipboardManager: ClipboardManager by lazy {
        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        Logger.d(TAG, "onCreate service start")
//
//        startForegroundService(
//            Intent(this@MainActivity, ClipboardService::class.java)
//        )
        MobileAds.initialize(this@MainActivity)
        numOfShow = getStateOFRewardedAD()
        Logger.d(TAG, "(getStateOFRewardedAD) numOfShow = $numOfShow")


        launch_btn.setOnClickListener {
            startLoadingAnimation()

//            Thread.sleep(3000)
//            endLoadingAnimation()

//            loadRewardedAd()


//            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
//
//
//                .setOnFinishListener {
//
//                    //load rewarded ADS
//
////                    loadRewardedAd()
//
//
////                    startMyService()
//
//                }.doAction()

        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           startForegroundService(Intent(this, ClipboardService::class.java))
//        } else {
//           startService(Intent(this, ClipboardService::class.java))
//        }


    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus || animationStarted) {
            return
        }

        animate()
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onStart() {
        super.onStart()
        showBannerAds()
    }


    private fun animate() {
        ViewCompat.animate(img_logo)
            .translationY(-250f)
            .setStartDelay(STARTUP_DELAY.toLong())
            .setDuration(ANIM_ITEM_DURATION.toLong()).setInterpolator(
                DecelerateInterpolator(1.2f)

            )
            .start()

        container.visibility = View.VISIBLE
        for (i in 0 until container.childCount) {
            val v = container.getChildAt(i)
            val viewAnimator: ViewPropertyAnimatorCompat = if (v !is Button) {
                ViewCompat.animate(v)
                    .translationY(50f).alpha(1f)
                    .setStartDelay((ITEM_DELAY * i + 500).toLong())
                    .setDuration(1000)
            } else {
                ViewCompat.animate(v)
                    .scaleY(1f).scaleX(1f)
                    .setStartDelay((ITEM_DELAY * i + 500).toLong())
                    .setDuration(500)
            }
            viewAnimator.setInterpolator(DecelerateInterpolator()).start()
        }
    }


    //videos ads
    private fun loadRewardedAd() {
        Logger.d(TAG, "(loadRewardedAd) called.")

        try {
            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                this, AD_REWARDEDED_ID, adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {

                        mAdIsFailed = true

//                        endLoadingAnimation()
                        Logger.e(TAG, "(loadRewardedAd) FailedToLoad . ${adError.message}")
                        mRewardedAd = null
                        mAdIsLoading = false
                        val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                                "message: ${adError.message}"
                        Logger.e(TAG, "(loadRewardedAd) FailedToLoad . $error")

//                        Toast.makeText(
//                            this@MainActivity,
//                            "FailedToLoad with error $error",
//                            Toast.LENGTH_SHORT
//                        ).show()

//                        startMyService()

                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd?) {
                        mRewardedAd = rewardedAd

                        Logger.d("loadAd", "Ad was loaded.")

                        //**************************
//                        if (rewardedAd != null) {
//                            endLoadingAnimation()
//
//
//
//
//                        }


                        //start the service here


                        //**************************


                        mAdIsLoading = false

                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.d(TAG, "showAds : catch $e")
        }


    }


    //Rewarded Ad
    private fun showRewardedAd(rewardedAd: RewardedAd?) {
        Logger.d(TAG, "(showRewardedAd) called.")

        //show ads
        if (rewardedAd != null) {

            rewardedAd.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Logger.d(TAG, "showRewardedAd Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
//                        rewardedAd = null
                        mRewardedAd = null
//                        mAdIsLoading = false
//                                shouldPlay = true
//                                loadAd()


                        //start service (1)
                        startMyService()


                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Logger.d(TAG, "showRewardedAd Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
//                        rewardedAd = null
                        mRewardedAd = null

                        //start service (2)
                        startMyService()

                    }

                    override fun onAdShowedFullScreenContent() {

                        Logger.d(TAG, "showRewardedAd Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                        mRewardedAd = null
//                        mAdIsLoading = true

                    }
                }
            mRewardedAd?.show(this, OnUserEarnedRewardListener() {

                fun onUserEarnedReward(rewardItem: RewardItem) {
//                    var rewardAmount = rewardItem.getReward()
                    var rewardType = rewardItem.type
                    Logger.d(TAG, "User earned the reward.")
                }
            })
//            super.finish()
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
//            super.finish()
        }


    }


    //Interstitial ads

    private fun loadInterstitialAd() {
        Logger.d(TAG, "(loadInterstitialAd) called.")


        val adRequest = AdRequest.Builder().build()


        InterstitialAd.load(
            this, AD_InterstitialAd_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Logger.d(TAG, "(loadInterstitialAd) loadAd is fail cause = ${adError.message} ")
                    mInterstitialAd = null
                    mAdIsFailed = true
//                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"

                    Logger.d(TAG, "(loadInterstitialAd) loadAd is fail $error ")

                    Toast.makeText(
                        this@MainActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Logger.d(TAG, "(loadInterstitialAd) onAdLoaded is sucsess  ")

                    mInterstitialAd = interstitialAd
//                    mAdIsLoading = false


                }
            }
        )
    }

    private fun showInterstitialAd() {
        Logger.d(TAG, "(showRewardedAd) called.")

        //show Interstitial ads
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Logger.d(TAG, "showInterstitial Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mInterstitialAd = null

                        startMyService()

                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Logger.d(TAG, "showInterstitial Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        startMyService()

                        mInterstitialAd = null

                    }

                    override fun onAdShowedFullScreenContent() {
                        Logger.d(TAG, "showInterstitial Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                        mInterstitialAd = null

                    }
                }
            mInterstitialAd?.show(this)
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onDestroy() {
        if (mRewardedAd != null) {
            mRewardedAd = null
//            changeOrientation()

        }
        if (mInterstitialAd != null) {
            mInterstitialAd = null
        }

        if (this.bannerAdShowed) {
            adView.destroy()
            entered_learn_ad_container.visibility = View.GONE
        }

        super.onDestroy()
    }


    private fun startLoadingAnimation() {
        loading_btn.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                Logger.d(TAG, "(startLoadingAnimation) onAnimationRepeat.")

            }

            override fun onAnimationEnd(animation: Animator?) {
                Logger.d(TAG, "(startLoadingAnimation) onAnimationEnd.")

                //Add your code here for animation end
                when {
                    (mRewardedAd != null || mInterstitialAd != null) -> {
                        Logger.d(
                            TAG,
                            "(startLoadingAnimation) onAnimationEnd. mRewardedAd or mInterstitialAd is not null"
                        )

                        //   loading_btn.loop(false)
                        loading_btn.removeAllAnimatorListeners()
                        endLoadingAnimation()

                    }
                    mAdIsFailed -> {
                        Logger.d(TAG, "(startLoadingAnimation) onAnimationEnd. load ads is fail")
                        loading_btn.removeAllAnimatorListeners()
                        endLoadingAnimation()
                    }
                    else -> {
                        loading_btn.repeatCount = 1
                        loading_btn.playAnimation()

                    }
                }

            }

            override fun onAnimationCancel(animation: Animator?) {
                Logger.d(TAG, "(startLoadingAnimation) onAnimationCancel.")

            }

            override fun onAnimationStart(animation: Animator?) {
                Logger.d(TAG, "(startLoadingAnimation) onAnimationStart.")

                if (!adLoadCalled) choseAdToLoad(numOfShow)

            }

        })
        launch_btn.visibility = View.GONE
        loading_btn.visibility = View.VISIBLE

        loading_btn.setMinAndMaxProgress(0f, 0.58f)
        loading_btn.playAnimation()


//        loading_btn.setMinAndMaxFrame(1,8)

//        loading_btn.repeatMode = LottieDrawable.RESTART


//        // Custom animation speed or duration.
//        val animator = ValueAnimator.ofFloat(0f, 0.6f)
//        animator.addUpdateListener {animation ->
//            loading_btn.progress = animation.animatedValue as Float
//        }
//        if(loading_btn.progress == 0f){
//            animator.start();
//        }else{
//            loading_btn.progress = 0f;
//        }

    }


    private fun endLoadingAnimation() {
        Logger.d(TAG, "(endLoadingAnimation) called.")
        loading_btn.repeatCount = 0


        loading_btn.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                Logger.d(TAG, "(endLoadingAnimation) onAnimationRepeat.")

            }

            override fun onAnimationEnd(animation: Animator?) {
                Logger.d(TAG, "(endLoadingAnimation) onAnimationEnd.")
//                loading_btn.cancelAnimation()
//                loading_btn.repeatCount = 0
                loading_btn.visibility = View.INVISIBLE
                if (mRewardedAd != null || mInterstitialAd != null) {
                    choseAdToShow(numOfShow)

                } else if (mAdIsFailed) {
                    startMyService()
                }


            }

            override fun onAnimationCancel(animation: Animator?) {
                Logger.d(TAG, "(endLoadingAnimation) onAnimationCancel.")

            }

            override fun onAnimationStart(animation: Animator?) {
                Logger.d(TAG, "(endLoadingAnimation) onAnimationStart.")
                loading_btn.repeatCount = 0

            }

        })

        loading_btn.setMinAndMaxProgress(0.58f, 1.0f)

        loading_btn.playAnimation()


    }

    //fun to chose what service will start


    private fun startMyService() {

        Logger.d(TAG, "(startMyService) called.")

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            checkUserPermissionAndShowFB()
        } else {
//        startService(
//            Intent(this@MainActivity, ClipboardService::class.java)
//        )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, ClipboardService::class.java)
                    .also {
                        it.action = ACTION_START_OR_RESUME_SERVICE
                    })

            } else {
                startService(Intent(this, ClipboardService::class.java).also {
                    it.action = ACTION_START_OR_RESUME_SERVICE
                })
            }
        }
        finish()


//        Intent(this, ClipboardService::class.java).also {
////            it.action = action.name
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Logger.d(TAG,"Starting the service in >=26 Mode")
//                startForegroundService(it)
//                return
//            }
//            Logger.d(TAG,"Starting the service in < 26 Mode")
//            startService(it)
//        }
//        finish()


    }

    private fun choseAdToLoad(rewardedShowTime: Int) {

        Logger.d(TAG, "(choseAdToLoad): called")
        adLoadCalled = true

        if (rewardedShowTime < 1) {
            Logger.d(TAG, "(choseAdToLoad): will load RewardedAd")

            loadRewardedAd()

        } else {
            Logger.d(TAG, "(choseAdToLoad): will load InterstitialAd")

            loadInterstitialAd()

        }


    }

    private fun choseAdToShow(rewardedShowTime: Int) {
        Logger.d(TAG, "(choseAdToShow): called")

        if (rewardedShowTime < 1) {
            Logger.d(TAG, "(choseAdToShow): will show RewardedAd")

            changeStateOFRewardedAD(numOfShow + 1)

            showRewardedAd(mRewardedAd)

        } else {
            Logger.d(TAG, "(choseAdToShow): will show InterstitialAd")

            changeStateOFRewardedAD(numOfShow - 2)
            showInterstitialAd()

        }


    }


    private fun changeStateOFRewardedAD(rewardedShowTime: Int) {
        Logger.d(TAG, "(changeStateOFRewardedAD): called")

        val sharedPref = getSharedPreferences("RewardedAD", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("showed", rewardedShowTime)
        editor.apply()
    }

    private fun getStateOFRewardedAD(): Int {
        Logger.d(TAG, "(getStateOFRewardedAD): called")

        val sharedPref =
            getSharedPreferences("RewardedAD", Context.MODE_PRIVATE)
        return sharedPref.getInt("showed", 0)
    }


    private fun showBannerAds() {

        try {

            adView = AdView(this)
            entered_learn_ad_container.addView(adView)

            adView.adSize = AdSize.BANNER
            adView.adUnitId = AD_BANNER_ID
            val adRequest = AdRequest.Builder()
                .build()

            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
//                        ad_viewOffline.visibility = View.VISIBLE
                    entered_learn_ad_container.visibility = View.VISIBLE
                    bannerAdShowed = true
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
//                        ad_viewOffline.visibility = View.GONE
                    entered_learn_ad_container.visibility = View.GONE
                    Logger.d("showAds", " : catch " + adError.toString())

                }
            }

            entered_learn_ad_container.visibility = View.VISIBLE
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            entered_learn_ad_container.visibility = View.GONE
            e.printStackTrace()
            Logger.d("showAds", " : catch " + e)
        }


    }

    private fun hideAds() {
        adView.pause()

//        ad_viewOffline.visibility = View.GONE
        entered_learn_ad_container.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        if (this.bannerAdShowed) hideAds()


    }

    //for moving button
    private fun checkUserPermissionAndShowFB() {
        Logger.d(TAG, "(checkUserPermissionAndShowFB) called.")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE)
        } else
//            startFloatingWidgetService()
            showCustomUsingKotlinDsl()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
//                startFloatingWidgetService()
                showCustomUsingKotlinDsl()
            else
                Toast.makeText(
                    this,
                    resources.getString(R.string.draw_other_app_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startFloatingWidgetService() {
        startService(Intent(this@MainActivity, FloatingWidgetService::class.java))
        finish()
    }

    private fun showCustomUsingKotlinDsl() {
        AppHead.create(R.drawable.ic_happy) {
            headView {

                layoutRes(R.layout.app_head, R.id.headImageView)
                onClick { onFloatingDialog() }
                onLongClick { Logger.d(TAG, "showCustomUsingKotlinDsl") }
                alpha(0.9f)
                allowBounce(false)
                onFinishInflate { Logger.d(TAG, "onFinishHeadViewInflate") }
                onDismiss { Logger.d(TAG, "onDismiss") }
                dismissOnClick(true)
                preserveScreenLocation(true)
            }
//            badgeView {
//                count("100")
//                position(BadgeView.Position.TOP_END)
//            }
            dismissView {
                alpha(0.5f)
                scaleRatio(1.0)
                drawableRes(R.drawable.ic_close_black)
                onFinishInflate { Logger.d(TAG, "onFinishDismissViewInflate") }
                setupImage { }
            }
        }.show(this)
    }


    private fun showFloatingDialog(action: String) {

        Logger.d(TAG, "showFloatingDialog ")

        var translateTXT = ""
        val builder = AlertDialog.Builder(this)

        val itemView: View =
            LayoutInflater.from(this).inflate(R.layout.layout_floating_translate, null)


        //get copied text if found
        if (getCopiedTxtFromClipboard().isNotBlank()) {
            itemView.img_paste.visibility = View.VISIBLE
            itemView.img_paste.setOnClickListener {
                ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                    .setOnFinishListener {
                        itemView.floating_original_txt.setText(getCopiedTxtFromClipboard())

                    }.doAction()

            }
        }


        //when translate button pressed
        itemView.floating_translate_btn.setOnClickListener {
            val originalTXT = itemView.floating_original_txt.text.toString()
            if (originalTXT.isNotBlank()) {
                try {
                    translateTXT = translateTxt(originalTXT)
                    itemView.floating_translated_txt.text = translateTXT
                    itemView.img_copy.visibility = View.VISIBLE
                } catch (e: InvalidTextException) {
                    Toast.makeText(
                        this,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "لا يوجد نص للتحويل",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        //when encrypt button pressed
        itemView.floating_encrypt_btn.setOnClickListener {
            val originalTXT = itemView.floating_original_txt.text.toString()
            if (originalTXT.isNotBlank()) {
                translateTXT = encryptTxt(originalTXT)
                itemView.floating_translated_txt.text = translateTXT
                itemView.img_copy.visibility = View.VISIBLE

            } else {
                Toast.makeText(
                    this,
                    "لا يوجد نص للتحويل",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        itemView.img_copy.setOnClickListener {

            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    val txtToCopy = itemView.floating_translated_txt.text.toString()

                    doCopy(txtToCopy)
                    if (!itemView.img_paste.isVisible)
                        itemView.img_paste.visibility = View.VISIBLE
                }.doAction()


        }


        builder.setView(itemView)
        val translateDialog = builder.create()
//        translateDialog.setCanceledOnTouchOutside(false)
        translateDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = translateDialog.window
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.windowAnimations = R.style.DialogAnimation

        translateDialog.setCancelable(false)
        translateDialog.setCanceledOnTouchOutside(false)
        itemView.okay_btn.setOnClickListener {
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    translateDialog.dismiss()

                }.doAction()


        }
        when (action) {
            Constants.FLOATING_DIALOG_ACTION_START -> {
                translateDialog.show()


            }
            Constants.FLOATING_DIALOG_ACTION_END -> {
                translateDialog.dismiss()
//                  finishAfterTransition()

            }
        }
        translateDialog.setOnDismissListener {
            Logger.d(TAG, "showFloatingDialog ")
//            sendBroadcast(Intent(Constants.ENCRYPT_ACTION).putExtra("copied", true))
            showCustomUsingKotlinDsl()
            finishAfterTransition()

        }


    }

    private fun translateTxt(originalTXT: String): String {
        val translate = TranslateMessageIMPL()
        return translate.translateTxt(originalTXT)
    }

    private fun encryptTxt(originalTXT: String): String {
        val encryption = EncryptionMessageIMPL()
        return encryption.encryptTxt(originalTXT)
    }


    private fun onFloatingDialog() {
        Logger.d(TAG, "onFloatingWidgetClick click isViewCollapsed  = ( $isViewCollapsed )")

        if (!isViewCollapsed) {
//
            val translateIntent = Intent(this, Translate::class.java).apply {
                action = Constants.FLOATING_DIALOG_ACTION_START
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(translateIntent)
//            supportFragmentManager.fragments.add(DialogFragment.newInstance("tst 1", "tst 2"))

//val frag = DialogFragment.newInstance("tst 1", "tst 2")
//            val f = supportFragmentManager.beginTransaction().add(frag,"ok").commit()


            isViewCollapsed = true

//            collapsedView!!.visibility = View.GONE

//            showSettingDialog()

//            expandedView!!.visibility = View.VISIBLE
        } else {
            val translateIntent = Intent(this, Translate::class.java).apply {
                action = Constants.FLOATING_DIALOG_ACTION_END
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(translateIntent)
            isViewCollapsed = false

        }

    }

    private fun getCopiedTxtFromClipboard(): String {
        return try {
            val clip: ClipData = mClipboardManager.primaryClip!!
            (clip.getItemAt(0).text).toString()
        } catch (e: Exception) {
            Logger.d(TAG, e.message)
            ""

        }

    }

    private fun doCopy(textToCopy: String) {
        try {

            val clip = ClipData.newPlainText("toPaste", textToCopy)
            mClipboardManager.setPrimaryClip(clip)
            Toast.makeText(
                this,
                "تم النسخ",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            Logger.d(TAG, e.message)
            Toast.makeText(
                this,
                "some thing go wrong",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    companion object {
        private const val DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222
    }
}
