package com.m37moud.mynewlang.ui

import TranslateMessageIMPL
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
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
import kotlinx.android.synthetic.main.floating_widget_layout.*
import kotlinx.android.synthetic.main.layout_floating_translate.view.*
import kotlinx.android.synthetic.main.layout_translate_app.view.*
import kotlinx.android.synthetic.main.layout_translate_app.view.okay_btn
import kotlinx.android.synthetic.main.layout_translate_app.view.translated_txt


private const val TAG = "Translate"

class Translate : AppCompatActivity() {
    private var encryptTXT = ""
    private var translateTXT = ""
    private lateinit var translate: TranslateMessageIMPL
    private var isViewCollapsed = false

    private val mClipboardManager: ClipboardManager by lazy {
        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        NotificationManagerCompat.from(this).cancel(1001)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_translate)

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
                if (!encryptTXT.isNullOrBlank()) {

                    try {
                        translate = TranslateMessageIMPL()
                        translateTXT = translateTxt(encryptTXT)
                        if (!translateTXT.isNullOrBlank()) {
                            showSettingDialog(encryptTXT, translateTXT)
                        }

                    } catch (e: InvalidTextException) {
                        Toast.makeText(this@Translate, e.message, Toast.LENGTH_SHORT).show()
                        sendBroadcast(Intent(Constants.ENCRYPT_ACTION).putExtra("copied", true))
                        finish()

                    }


                }
            }
        }


    }


    override fun onStop() {
        super.onStop()
//        unregisterReceiver(notifyBroadcast)
    }

    override fun onBackPressed() {
        Toast.makeText(this@Translate, "اضغط حسنا للقفل", Toast.LENGTH_SHORT).show()

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
                    {
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
                    translateDialog.dismiss()

                }.doAction()


        }
        when (action) {
            FLOATING_DIALOG_ACTION_START -> {
                translateDialog.show()


            }
            FLOATING_DIALOG_ACTION_END -> {
                translateDialog.dismiss()
                  finishAfterTransition()

            }
        }
        translateDialog.setOnDismissListener {
            Logger.d(TAG, "showFloatingDialog ")
//            sendBroadcast(Intent(Constants.ENCRYPT_ACTION).putExtra("copied", true))
            showCustomUsingKotlinDsl()
            finishAfterTransition()

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
        }.show(this )
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

    private fun showSettingDialog(encryptTXT: String, translateTXT: String) {
        Logger.d(TAG, "showSettingDialog ")

        val builder = AlertDialog.Builder(this)

        val itemView: View = LayoutInflater.from(this).inflate(R.layout.layout_translate_app, null)

        itemView.encrypt_txt.text = encryptTXT
        itemView.translated_txt.text = translateTXT
        Logger.d(TAG, "(showSettingDialog) will set this $translateTXT")


//
//        val popUp = PopupWindow(
//            itemView, LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT, false
//        )
//        popUp.isTouchable = true
//        popUp.isFocusable = true
//        popUp.isOutsideTouchable = true
//        popUp.showAsDropDown(img_main_setting)

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
                    finishAfterTransition()

                }.doAction()


        }



        translateDialog.show()
        translateDialog.setOnDismissListener {
            Logger.d(TAG, "showSettingDialog ")
            sendBroadcast(Intent(Constants.ENCRYPT_ACTION).putExtra("copied", true))

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

}