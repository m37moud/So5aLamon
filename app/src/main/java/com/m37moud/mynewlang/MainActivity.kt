package com.m37moud.mynewlang

import android.animation.Animator
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.huawei.hms.ads.reward.Reward
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import com.google.android.gms.ads.*
import com.huawei.hms.ads.*
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener
import com.m37moud.mynewlang.ui.Translate
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Logger
import com.sha.apphead.AppHead
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_floating_translate.view.*
import kotlinx.android.synthetic.main.layout_translate_app.view.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var animationStarted = false
    val STARTUP_DELAY = 300
    val ANIM_ITEM_DURATION = 1000
    val ITEM_DELAY = 300


    //ads
    private var mRewardedAd: RewardAd? = null
    private val defaultScore = 10
    private var score = 1
    private var mAdIsLoading: Boolean = false
    private var adLoadCalled: Boolean = false //detect first load
    private var mAdIsFailed: Boolean = false //detect if ad is failed
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adView: BannerView   //banner ads
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
//        MobileAds.initialize(this@MainActivity)
        HwAds.init(this@MainActivity)
        numOfShow = getStateOFRewardedAD()
        Logger.d(TAG, "(getStateOFRewardedAD) numOfShow = $numOfShow")


        launch_btn.setOnClickListener {
            startLoadingAnimation()


        }


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


    //videos Reward ads
    /**
     * Load a rewarded ad.
     */
    private fun loadRewardedAd() {
        Logger.d(TAG, "(loadRewardedAd) (huawie) called.")
        try {

        if (mRewardedAd == null) {
            mRewardedAd = RewardAd(this@MainActivity, getString(R.string.ad_id_reward))
        }
        val rewardAdLoadListener: RewardAdLoadListener = object : RewardAdLoadListener() {
            override fun onRewardAdFailedToLoad(errorCode: Int) {
                mAdIsFailed = true

                Logger.e(TAG, "(onRewardAdFailedToLoad) (huawie) FailedToLoad . ${errorCode}")
                mRewardedAd = null
                mAdIsLoading = false


            }

            override fun onRewardedLoaded() {
                Logger.d(TAG, "onRewardedLoaded (huawie) Ad was loaded.")
                mAdIsLoading = false
            }
        }
        mRewardedAd!!.loadAd(AdParam.Builder().build(), rewardAdLoadListener)
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.d(TAG, "loadRewardedAd (huawie) : catch $e")
        }
    }

//    private fun loadRewardedAd() {
//        Logger.d(TAG, "(loadRewardedAd) called.")
//
//        try {
//            val adRequest = AdRequest.Builder().build()
//
//            RewardedAd.load(
//                this, getString(R.string.ad_id_reward), adRequest,
//                object : RewardedAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//
//                        mAdIsFailed = true
//
////                        endLoadingAnimation()
//                        Logger.e(TAG, "(loadRewardedAd) FailedToLoad . ${adError.message}")
//                        mRewardedAd = null
//                        mAdIsLoading = false
//                        val error = "domain: ${adError.domain}, code: ${adError.code}, " +
//                                "message: ${adError.message}"
//                        Logger.e(TAG, "(loadRewardedAd) FailedToLoad . $error")
//
//
//                    }
//
//                    override fun onAdLoaded(rewardedAd: RewardedAd?) {
//                        mRewardedAd = rewardedAd
//
//                        Logger.d("loadAd", "Ad was loaded.")
//
//                        //**************************
////                        if (rewardedAd != null) {
////                            endLoadingAnimation()
////
////
////
////
////                        }
//
//
//                        //start the service here
//
//
//                        //**************************
//
//
//                        mAdIsLoading = false
//
//                    }
//                }
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Logger.d(TAG, "showAds : catch $e")
//        }
//
//
//    }


    /**
     * Display a rewarded ad.
     */
    private fun showRewardedAd(rewardedAd: RewardAd?) {
        Logger.d(TAG, "(showRewardedAd) (huawie) called.")


        if (rewardedAd!!.isLoaded) {
            rewardedAd!!.show(this@MainActivity, object : RewardAdStatusListener() {
                override fun onRewardAdClosed() {
                    Logger.d(TAG, "(showRewardedAd) onRewardAdClosed (huawie) Ad was closed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
//                        rewardedAd = null
                    mRewardedAd = null
                    //start service (1)
                    startMyService()
                }

                override fun onRewardAdFailedToShow(errorCode: Int) {
                    Logger.d(TAG, "(showRewardedAd) onRewardAdFailedToShow (huawie) Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
//                        rewardedAd = null
                    mRewardedAd = null

                    //start service (2)
                    startMyService()
                }

                override fun onRewardAdOpened() {
                    Logger.d(TAG, "(showRewardedAd) onRewardAdOpened (huawie) Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                    mRewardedAd = null
                }

                override fun onRewarded(reward: Reward) {
                    Logger.d(TAG, "(showRewardedAd) onRewarded(huawie) User earned the reward.")
                    // You are advised to grant a reward immediately and at the same time, check whether the reward
                    // takes effect on the server. If no reward information is configured, grant a reward based on the
                    // actual scenario.
                    val addScore = if (reward.amount == 0) defaultScore else reward.amount
                    score += addScore
//                    setScore(score)
//                    loadRewardAd()
                }
            })
        }else {
            Logger.e(TAG ," (showRewardedAd) (huawie) Ad wasn't loaded." )
        }
    }

    //Rewarded Ad
//    private fun showRewardedAd(rewardedAd: RewardedAd?) {
//        Logger.d(TAG, "(showRewardedAd) called.")
//
//        //show ads
//        if (rewardedAd != null) {
//
//            rewardedAd.fullScreenContentCallback =
//                object : FullScreenContentCallback() {
//                    override fun onAdDismissedFullScreenContent() {
//                        Logger.d(TAG, "showRewardedAd Ad was dismissed.")
//                        // Don't forget to set the ad reference to null so you
//                        // don't show the ad a second time.
////                        rewardedAd = null
//                        mRewardedAd = null
////                        mAdIsLoading = false
////                                shouldPlay = true
////                                loadAd()
//
//
//                        //start service (1)
//                        startMyService()
//
//
//                    }
//
//                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                        Logger.d(TAG, "showRewardedAd Ad failed to show.")
//                        // Don't forget to set the ad reference to null so you
//                        // don't show the ad a second time.
////                        rewardedAd = null
//                        mRewardedAd = null
//
//                        //start service (2)
//                        startMyService()
//
//                    }
//
//                    override fun onAdShowedFullScreenContent() {
//
//                        Logger.d(TAG, "showRewardedAd Ad showed fullscreen content.")
//                        // Called when ad is dismissed.
//                        mRewardedAd = null
////                        mAdIsLoading = true
//
//                    }
//                }
//            mRewardedAd?.show(this, OnUserEarnedRewardListener() {
//
//                fun onUserEarnedReward(rewardItem: RewardItem) {
////                    var rewardAmount = rewardItem.getReward()
//                    var rewardType = rewardItem.type
//                    Logger.d(TAG, "User earned the reward.")
//                }
//            })
////            super.finish()
//        } else {
//            Logger.e(TAG ,"Ad wasn't loaded." )
////            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
////            super.finish()
//        }
//
//
//    }


    //Interstitial ads
    private var interstitialImg = false
    private val adId: String
        get() = if (interstitialImg) {
            interstitialImg = false
            getString(R.string.image_ad_id)
        } else {
            interstitialImg = true
            getString(R.string.video_ad_id)
        }
    private val interstitiAladListener: AdListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Logger.d(TAG, "(loadInterstitialAd) (huawie) onAdLoaded is sucsess  ")
            // Display an interstitial ad.
//            mInterstitialAd = null
        }

        override fun onAdFailed(errorCode: Int) {
            Logger.d(TAG, "(huawie) Ad load failed with error code: $errorCode")
            mInterstitialAd = null
            mAdIsFailed = true
        }

        override fun onAdClosed() {
            super.onAdClosed()
            Logger.d(TAG, "(huawie) onAdClosed")
            mInterstitialAd = null

            startMyService()
        }

        override fun onAdClicked() {
            Logger.d(TAG, "(huawie)  onAdClicked")
            super.onAdClicked()
        }

        override fun onAdOpened() {
            Logger.d(TAG, "(huawie) onAdOpened")
            super.onAdOpened()
        }
    }
    private fun loadInterstitialAd() {
        Logger.d(TAG, "(loadInterstitialAd (huawie) ) called.")

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd!!.adId = adId
        mInterstitialAd!!.adListener = interstitiAladListener
        val adParam = AdParam.Builder().build()
        mInterstitialAd!!.loadAd(adParam)
    }

    private fun showInterstitialAd() {
        Logger.d(TAG, "(showInterstitialAd (huawie) ) called.")

        // Display an interstitial ad.
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded) {
            mInterstitialAd!!.show(this)
        } else {
            Logger.e(TAG ," (huawie) Ad wasn't loaded." )
        }
    }




    override fun onDestroy() {
        if (mRewardedAd != null) {
            mRewardedAd = null

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
        checkUserPermissionAndShowFB()

        finish()


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
        Logger.d(TAG, "(showBannerAds (huawie) ): called")

        try {

            adView = BannerView(this@MainActivity)
            entered_learn_ad_container.addView(adView)

            adView.bannerAdSize  = BannerAdSize.BANNER_SIZE_360_57
            adView.adId = getString(R.string.banner_ad_id)
            adView.setBannerRefresh(60)
            adView.setBackgroundColor(Color.TRANSPARENT)
            val adRequest = AdParam.Builder()
                .build()

            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
//                        ad_viewOffline.visibility = View.VISIBLE
                    entered_learn_ad_container.visibility = View.VISIBLE
                    bannerAdShowed = true
                }

                override fun onAdFailed(errorcode: Int) {
//                        ad_viewOffline.visibility = View.GONE
                    entered_learn_ad_container.visibility = View.GONE
//                    Logger.d("showAds", " : catch " + adError.toString())

                }
            }

            entered_learn_ad_container.visibility = View.VISIBLE
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            entered_learn_ad_container.visibility = View.GONE
            e.printStackTrace()
            Logger.d(TAG, "(showBannerAds (huawie) : catch " + e)
        }


    }

    private fun hideAds() {
        adView.pause()

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
            dismissView {
                alpha(0.5f)
                scaleRatio(1.0)
                drawableRes(R.drawable.ic_close_black)
                onFinishInflate { Logger.d(TAG, "onFinishDismissViewInflate") }
                setupImage { }
            }
        }.show(this)
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


            isViewCollapsed = true

        } else {
            val translateIntent = Intent(this, Translate::class.java).apply {
                action = Constants.FLOATING_DIALOG_ACTION_END
            }
            startActivity(translateIntent)
            isViewCollapsed = false

        }

    }



    companion object {
        private const val DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222
    }
}
