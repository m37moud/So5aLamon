package com.m37moud.mynewlang.ui

import TranslateMessageIMPL
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView
import com.m37moud.mynewlang.R
import com.m37moud.mynewlang.data.EncryptionMessageIMPL
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Constants.Companion.FLOATING_DIALOG_ACTION_END
import com.m37moud.mynewlang.util.Constants.Companion.FLOATING_DIALOG_ACTION_START
import com.m37moud.mynewlang.util.Constants.Companion.ORIGINAL_TXT
import com.m37moud.mynewlang.util.InvalidTextException
import com.m37moud.mynewlang.util.Logger
import com.sha.apphead.AppHead
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_floating_translate.*
import kotlinx.android.synthetic.main.layout_floating_translate.view.*


private const val TAG = "Translate"

class Translate : AppCompatActivity() {
    private var encryptTXT = ""
    private var translateTXT = ""
    private lateinit var translate: TranslateMessageIMPL
    private var isViewCollapsed = false

    private var mClipboardManager: ClipboardManager? = null

    private lateinit var adView: BannerView   //banner ads
    private var bannerAdShowed = false         //banner ads
    private var isFloatingDialogShow = false         //banner ads


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        NotificationManagerCompat.from(this).cancel(1001)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_translate)
        loadBannerAds()

        val intent = intent

        encryptTXT = intent?.getStringExtra(ORIGINAL_TXT).toString()

        val action = intent.action
        Logger.d(TAG, " action TEXT IS ${intent.action}")

        when (action) {
            FLOATING_DIALOG_ACTION_START -> {
                showFloatingDialog(action)

            }
            FLOATING_DIALOG_ACTION_END -> {
                showFloatingDialog(action)

            }
            else -> {
                Toast.makeText(this@Translate, "some thing go wrong", Toast.LENGTH_SHORT).show()


            }
        }


    }

    private fun hideAds() {
        if (isFloatingDialogShow && this.bannerAdShowed) {
            adView.pause()

//            floating_ad_container.visibility = View.GONE
            isFloatingDialogShow = false
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFloatingDialogShow && this.bannerAdShowed) hideAds()

    }

    override fun onDestroy() {

        if (isFloatingDialogShow && this.bannerAdShowed) {
            adView.destroy()
//            floating_ad_container.visibility = View.GONE
        }

        super.onDestroy()
    }

    override fun onBackPressed() {
        Toast.makeText(this@Translate, "اضغط حسنا للقفل", Toast.LENGTH_SHORT).show()

    }

    private fun showFloatingDialog(action: String) {
        isFloatingDialogShow = true

        Logger.d(TAG, "showFloatingDialog ")

        var translateTXT = ""
        val builder = AlertDialog.Builder(this)

        val itemView: View =
            LayoutInflater.from(this).inflate(R.layout.layout_floating_translate, null)

        // show add if loaded

        showBannerAd(itemView.floating_ad_container)
        if (!adView.isLoading) {
            loadBannerAds()
            showBannerAd(itemView.floating_ad_container)
        }

        //get copied text if found
        if (getCopiedTxtFromClipboard().isNotBlank()) {
            itemView.img_paste.visibility = View.VISIBLE

        }
        itemView.img_paste.setOnClickListener {
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    Logger.d(TAG, "showFloatingDialog copy button is clicked")
                    if (getCopiedTxtFromClipboard().isNotBlank()) {
                        itemView.floating_original_txt.setText(getCopiedTxtFromClipboard())
                        itemView.floating_original_txt.setSelection(itemView.floating_original_txt.length())//placing cursor at the end of the text
                    }
                }.doAction()

        }

        itemView.img_undo.setOnClickListener {
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    itemView.floating_original_txt.setText("")
                    itemView.floating_translated_txt.text = ""
                    itemView.img_copy.visibility = View.INVISIBLE
                }.doAction()

        }

        itemView.floating_original_txt.addTextChangedListener {
            if (itemView.floating_original_txt.text.toString().isNullOrEmpty()) {
                itemView.img_undo.visibility = View.INVISIBLE
                itemView.img_paste.visibility = View.VISIBLE
            } else {
                itemView.img_undo.visibility = View.VISIBLE
                itemView.img_paste.visibility = View.INVISIBLE
            }


        }


        //when translate button pressed
        itemView.floating_translate_btn.setOnClickListener {
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    val originalTXT = itemView.floating_original_txt.text.toString()
                    if (originalTXT.isNotBlank()) {
                        if (!Constants.textContainsArabic(originalTXT)) {
                            Toast.makeText(this, "عربى بس", Toast.LENGTH_SHORT).show()

                        } else {
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
                        }

                    } else {
                        Toast.makeText(
                            this,
                            "لا يوجد نص للتحويل",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.doAction()


        }

        //when encrypt button pressed
        itemView.floating_encrypt_btn.setOnClickListener {
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    val originalTXT = itemView.floating_original_txt.text.toString()
                    if (originalTXT.isNotBlank()) {
                        if (!Constants.textContainsArabic(originalTXT)) {
                            Toast.makeText(this, "عربى بس", Toast.LENGTH_SHORT).show()

                        } else {
                            translateTXT = encryptTxt(originalTXT)
                            itemView.floating_translated_txt.text = translateTXT
                            itemView.img_copy.visibility = View.VISIBLE
                        }


                    } else {
                        Toast.makeText(
                            this,
                            "لا يوجد نص للتحويل",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.doAction()


        }

        itemView.img_copy.setOnClickListener {

            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    val txtToCopy = itemView.floating_translated_txt.text.toString()

                    doCopy(txtToCopy)
                    if (!itemView.img_paste.isVisible) {
                        itemView.img_paste.visibility = View.VISIBLE
                        itemView.img_undo.visibility = View.INVISIBLE
                    }
                }.doAction()


        }




        builder.setView(itemView)
        val translateDialog = builder.create()
//        translateDialog.setCanceledOnTouchOutside(false)
        translateDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = translateDialog.window
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.windowAnimations = R.style.DialogAnimation
        translateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        translateDialog.setCancelable(false)
        translateDialog.setCanceledOnTouchOutside(false)
        itemView.okay_btn.setOnClickListener {
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {
                    hideAds()
                    translateDialog.dismiss()

                }.doAction()


        }
        when (action) {
            FLOATING_DIALOG_ACTION_START -> {
                translateDialog.show()


            }
            FLOATING_DIALOG_ACTION_END -> {
                super.finish()
                showCustomUsingKotlinDsl()
                translateDialog.dismiss()

            }
        }
        translateDialog.setOnDismissListener {
            Logger.d(TAG, "showFloatingDialog ")
//            sendBroadcast(Intent(Constants.ENCRYPT_ACTION).putExtra("copied", true))
            super.finish()
            finishAfterTransition()
            showCustomUsingKotlinDsl()

        }


    }

    private fun loadBannerAds() {
        Logger.d(TAG, "(loadBannerAds (huawie) ): called")

        try {
            HwAds.init(this@Translate)

            adView = BannerView(this)

            adView.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57

            adView.adId = getString(R.string.banner_ad_id)
            adView.setBannerRefresh(60)
            adView.setBackgroundColor(Color.TRANSPARENT)
            val adRequest = AdParam.Builder()
                .build()
            adView.loadAd(adRequest)


        } catch (e: Exception) {
            e.printStackTrace()
            Logger.d(TAG, "(loadBannerAds (huawie) : catch " + e)
        }


    }

    private fun showBannerAd(container: FrameLayout) {
        Logger.d(TAG, "(showBannerAd (huawie) : called ${adView.isLoading} ")

        try {
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Logger.d(TAG, "(showBannerAd (huawie) : load success ")

                    container.addView(adView)

//                        ad_viewOffline.visibility = View.VISIBLE
                    container.visibility = View.VISIBLE
                    bannerAdShowed = true
                }

                override fun onAdFailed(errorcode: Int) {
//                        ad_viewOffline.visibility = View.GONE
                    Logger.d(TAG, "(showBannerAd (huawie) : load faild  $errorcode ")

                    container.visibility = View.GONE
                    Logger.d("showBannerAd", " : faild " + errorcode.toString())

                }
            }
        } catch (e: Exception) {
            Logger.d(TAG, "(showBannerAds (huawie) : catch " + e)

            container.visibility = View.GONE

        }
    }

    private fun showCustomUsingKotlinDsl() {
        //load banner
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
                drawableRes(R.drawable.ic_close_orange)
                onFinishInflate { Logger.d(TAG, "onFinishDismissViewInflate") }
                setupImage { }
            }
        }.show(this)
    }

    private fun onFloatingDialog() {
        Logger.d(TAG, "onFloatingWidgetClick click isViewCollapsed  = ( $isViewCollapsed )")

        if (!isViewCollapsed) {

            val translateIntent = Intent(this, Translate::class.java).apply {
                action = FLOATING_DIALOG_ACTION_START
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(translateIntent)
            isViewCollapsed = true

//            collapsedView!!.visibility = View.GONE

//            showSettingDialog()

//            expandedView!!.visibility = View.VISIBLE
        } else {
            val translateIntent = Intent(this, Translate::class.java).apply {
                action = Constants.FLOATING_DIALOG_ACTION_END
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(translateIntent)
            isViewCollapsed = false

        }

    }

    private fun getCopiedTxtFromClipboard(): String {
        Logger.d(TAG, "getCopiedTxtFromClipboard method called ")

        return try {
            if (mClipboardManager == null) {
                Logger.d(TAG, "getCopiedTxtFromClipboard method will init mClipboardManager ")


                mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager


            }

            if (!mClipboardManager?.hasPrimaryClip()!!) {
                Logger.d(TAG, "getCopiedTxtFromClipboard method hasnt clipboard ")

                ""
            } else {
                Logger.d(TAG, "getCopiedTxtFromClipboard method has data ")

                val clip: ClipData = mClipboardManager?.primaryClip!!
                (clip.getItemAt(0).text).toString()
            }

        } catch (e: Exception) {
            Logger.d(TAG, e.message)
            ""

        }

    }

    private fun doCopy(textToCopy: String) {
        try {
            if (mClipboardManager == null) {

                mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            }
            val clip = ClipData.newPlainText("toPaste", textToCopy)
            mClipboardManager?.setPrimaryClip(clip)
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

    private fun translateTxt(originalTXT: String): String {
        val translate = TranslateMessageIMPL()
        return translate.translateTxt(originalTXT)
    }

    private fun encryptTxt(originalTXT: String): String {
        val encryption = EncryptionMessageIMPL()
        return encryption.encryptTxt(originalTXT)
    }

}