package com.m37moud.mynewlang

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.m37moud.mynewlang.util.Constants.Companion.AD_InterstitialAd_ID
import com.m37moud.mynewlang.util.Constants.Companion.AD_REWARDEDAD_ID
import com.m37moud.responsivestories.util.Logger
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var animationStarted = false
    val STARTUP_DELAY = 300
    val ANIM_ITEM_DURATION = 1000
    val ITEM_DELAY = 300


    //ads
    private var mRewardedAd: RewardedAd? = null
    private var mAdIsLoading: Boolean = false


    //ads refrence
    private var mInterstitialAd: InterstitialAd? = null

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

        animate();
        super.onWindowFocusChanged(hasFocus)
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
            var viewAnimator: ViewPropertyAnimatorCompat = if (v !is Button) {
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
        Logger.d(TAG, "loadRewardedAd called.")


        try {
            var adRequest = AdRequest.Builder().build()


            RewardedAd.load(
                this, AD_REWARDEDAD_ID, adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {

//                        endLoadingAnimation()

                        Logger.d("loadAd", adError?.message)
                        mRewardedAd = null
                        mAdIsLoading = false
                        val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                                "message: ${adError.message}"
                        Toast.makeText(
                            this@MainActivity,
                            "onAdFailedToLoad() with error $error",
                            Toast.LENGTH_SHORT
                        ).show()

                        startMyService()

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

                        //TODO:start the service here

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

        //show ads
        if (rewardedAd != null) {

            rewardedAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Logger.d("loadAd", "showInterstitial Ad was dismissed.")
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
                        Logger.d("loadAd", "showInterstitial Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
//                        rewardedAd = null
                        mRewardedAd = null

                        //start service (2)
                        startMyService()

                    }

                    override fun onAdShowedFullScreenContent() {

                        Logger.d("loadAd", "showInterstitial Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                        mRewardedAd = null
//                        mAdIsLoading = true

                    }
                }
            mRewardedAd?.show(this, OnUserEarnedRewardListener() {

                fun onUserEarnedReward(rewardItem: RewardItem) {
//                    var rewardAmount = rewardItem.getReward()
                    var rewardType = rewardItem.type
                    Logger.d("loadAd", "User earned the reward.")
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


        var adRequest = AdRequest.Builder().build()


        InterstitialAd.load(
            this, AD_InterstitialAd_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Logger.d("loadAd", adError?.message)
                    mInterstitialAd = null
//                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Toast.makeText(
                        this@MainActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Logger.d("loadAd", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
//                    mAdIsLoading = false
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Logger.d("loadAd", "showInterstitial Ad was dismissed.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                mInterstitialAd = null
//                                shouldPlay = true

//                                loadAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                Logger.d("loadAd", "showInterstitial Ad failed to show.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                mInterstitialAd = null

                            }

                            override fun onAdShowedFullScreenContent() {
                                Logger.d("loadAd", "showInterstitial Ad showed fullscreen content.")
                                // Called when ad is dismissed.
                                mInterstitialAd = null

                            }
                        }

                }
            }
        )
    }

    private fun showInterstitialAd() {
        //show Interstitial ads
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
        }

    }


//    override fun finish() {
//
//        //show Interstitial ads
//        if (mInterstitialAd != null) {
//            mInterstitialAd?.show(this)
//            super.finish()
//        } else {
//            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
//            super.finish()
//        }
//
//        //show ads
//        if (mRewardedAd != null) {
////            shouldPlay = false
//            mRewardedAd?.fullScreenContentCallback =
//                object : FullScreenContentCallback() {
//                    override fun onAdDismissedFullScreenContent() {
//                        Logger.d("loadAd", "showInterstitial Ad was dismissed.")
//                        // Don't forget to set the ad reference to null so you
//                        // don't show the ad a second time.
//                        mRewardedAd = null
//                        mAdIsLoading = false
////                                shouldPlay = true
////                                loadAd()
//                    }
//
//                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                        Logger.d("loadAd", "showInterstitial Ad failed to show.")
//                        // Don't forget to set the ad reference to null so you
//                        // don't show the ad a second time.
//                        mRewardedAd = null
//                    }
//
//                    override fun onAdShowedFullScreenContent() {
//
//                        Logger.d("loadAd", "showInterstitial Ad showed fullscreen content.")
//                        // Called when ad is dismissed.
//                        mRewardedAd = null
//                        mAdIsLoading = true
//
//                    }
//                }
////            mAdIsLoading = true
//            mRewardedAd?.show(this, OnUserEarnedRewardListener() {
//
//                fun onUserEarnedReward(rewardItem: RewardItem) {
////                    var rewardAmount = rewardItem.getReward()
//                    var rewardType = rewardItem.type
//                    Logger.d("loadAd", "User earned the reward.")
//                }
//            })
//
//            super.finish()
//        } else {
//            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
//            super.finish()
//        }
//
//
//    }


    override fun onDestroy() {
        if (mRewardedAd != null) {
            mRewardedAd = null
//            changeOrientation()

        }
        if (mInterstitialAd != null) {
            mInterstitialAd = null
        }

        super.onDestroy()
    }



    private fun startLoadingAnimation() {
        loading_btn.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
                Logger.d(TAG, "onAnimationRepeat.")

            }

            override fun onAnimationEnd(animation: Animator?) {
                Logger.d(TAG, "onAnimationEnd.")

                //Add your code here for animation end
                if(mRewardedAd!=null){
                    endLoadingAnimation()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
                Logger.d(TAG, "onAnimationCancel.")

            }

            override fun onAnimationStart(animation: Animator?) {
                Logger.d(TAG, "onAnimationStart.")

                loadRewardedAd()
            }

        })
        launch_btn.visibility = View.GONE
        loading_btn.visibility = View.VISIBLE

        loading_btn.setMinAndMaxProgress(0f,0.58f)
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

        loading_btn.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
                Logger.d(TAG, "onAnimationRepeat.")

            }

            override fun onAnimationEnd(animation: Animator?) {
                Logger.d(TAG, "onAnimationEnd.")
                loading_btn.cancelAnimation()
                loading_btn.repeatCount = 0
                loading_btn.visibility = View.INVISIBLE
                showRewardedAd(mRewardedAd)

            }

            override fun onAnimationCancel(animation: Animator?) {
                Logger.d(TAG, "onAnimationCancel.")

            }

            override fun onAnimationStart(animation: Animator?) {
                Logger.d(TAG, "onAnimationStart.")

            }

        })


        loading_btn.setMinAndMaxProgress(0.58f,1.0f)

        loading_btn.playAnimation()

    }

    private fun startMyService() {
        startService(
            Intent(this@MainActivity, ClipboardService::class.java)
        )
        finish()
    }

}