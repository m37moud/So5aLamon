package com.m37moud.mynewlang

import android.animation.Animator
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


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var animationStarted = false
    val STARTUP_DELAY = 300
    val ANIM_ITEM_DURATION = 1000
    val ITEM_DELAY = 300


    //ads
    private val defaultScore = 10
    private var score = 1
    private var mAdIsLoading: Boolean = false
    private var adLoadCalled: Boolean = false //detect first load
    private var mAdIsFailed: Boolean = false //detect if ad is failed
    private var permissionRejected: Boolean = false //detect if ad is failed
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adView: BannerView   //banner ads
    private var bannerAdShowed = false//banner ads
    private var numOfShow = 0


    private var isViewCollapsed = false

    //end of ads refrence

    //cliboard


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        Logger.d(TAG, "onCreate huawie  start")
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





    //Interstitial ads
    private val adId: String
        get() = if (numOfShow < 1) {
            changeStateOFRewardedAD(numOfShow + 1)
//            interstitialImg = false
            getString(R.string.image_ad_id)
        } else {
            changeStateOFRewardedAD(numOfShow - 1)

//            interstitialImg = true
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
            Logger.e(TAG, "(huawie) Ad load failed with error code: $errorCode")
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
        adLoadCalled = true
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
            mAdIsFailed = true
            mInterstitialAd = null
            Logger.e(TAG, " (huawie) Ad wasn't loaded.")
        }
    }


    override fun onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd = null
        }

        if (this.bannerAdShowed) {
            adView.destroy()
            main_ad_container.visibility = View.GONE
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
                    ( mInterstitialAd != null) -> {
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
                    permissionRejected -> {
                        Logger.d(TAG, "(startLoadingAnimation) onAnimationEnd. permission rejected")
                        loading_btn.removeAllAnimatorListeners()

                        endLoadingAnimation()
                    }
                    else -> {
                        Logger.d(TAG, "(startLoadingAnimation) onAnimationEnd.  else")

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

                if (!adLoadCalled) loadInterstitialAd()

//                choseAdToLoad(numOfShow)

            }

        })
        launch_btn.visibility = View.GONE
        loading_btn.visibility = View.VISIBLE

        loading_btn.setMinAndMaxProgress(0f, 0.58f)
        loading_btn.playAnimation()


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
                if ( mInterstitialAd != null) {
                    Logger.d(TAG, "(endLoadingAnimation) onAnimationEnd. mRewardedAd or mInterstitialAd not null")

                    showInterstitialAd()
//                    choseAdToShow(numOfShow)

                } else if (mAdIsFailed) {
                    Logger.d(TAG, "(endLoadingAnimation) onAnimationEnd. mAdIsFailed = true")

                    startMyService()
                }else if (permissionRejected){
                    Logger.d(TAG, "(endLoadingAnimation) onAnimationEnd. permissionRejected = true")

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
            main_ad_container.addView(adView)

            adView.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
            adView.adId = getString(R.string.banner_ad_id)
            adView.setBannerRefresh(60)
            adView.setBackgroundColor(Color.TRANSPARENT)
            val adRequest = AdParam.Builder()
                .build()

            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
//                        ad_viewOffline.visibility = View.VISIBLE
                    main_ad_container.visibility = View.VISIBLE
                    bannerAdShowed = true
                }

                override fun onAdFailed(errorcode: Int) {
//                        ad_viewOffline.visibility = View.GONE
                    main_ad_container.visibility = View.GONE
//                    Logger.d("showAds", " : catch " + adError.toString())

                }
            }

            main_ad_container.visibility = View.VISIBLE
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            main_ad_container.visibility = View.GONE
            e.printStackTrace()
            Logger.d(TAG, "(showBannerAds (huawie) : catch " + e)
        }


    }

    private fun hideAds() {
        adView.pause()

        main_ad_container.visibility = View.GONE
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
        } else {
//            startFloatingWidgetService()
            showCustomUsingKotlinDsl()
//            this@MainActivity.finish()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
            if (Settings.canDrawOverlays(this)) {
//                startFloatingWidgetService()
                showCustomUsingKotlinDsl()
//                this@MainActivity.finish()


            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.draw_other_app_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                permissionRejected()
            }


        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun permissionRejected() {
        Logger.d(TAG, "(permissionRejected) called.")

        launch_btn.visibility = View.VISIBLE
        loading_btn.visibility = View.GONE
        txt_v1.visibility = View.GONE
        txt_v2.text = "لابد من اعطاء الاذن للبرنامج لكى يعمل"
        img_logo.setImageResource(R.drawable.ic_sad)
        permissionRejected = true
    }


    private fun showCustomUsingKotlinDsl() {
        Logger.d(TAG, "(showCustomUsingKotlinDsl) called.")

        super.finish()
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
                drawableRes(R.drawable.ic_close_orange)
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
