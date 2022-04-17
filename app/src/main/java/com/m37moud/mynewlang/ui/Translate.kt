package com.m37moud.mynewlang.ui

import TranslateMessageIMPL
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.mynewlang.R
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Constants.Companion.FLOATING_DIALOG_ACTION_END
import com.m37moud.mynewlang.util.Constants.Companion.FLOATING_DIALOG_ACTION_START
import com.m37moud.mynewlang.util.Constants.Companion.ORIGINAL_TXT
import com.m37moud.mynewlang.util.InvalidTextException
import com.m37moud.mynewlang.util.Logger
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        NotificationManagerCompat.from(this).cancel(1001)

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

        val builder = AlertDialog.Builder(this)

        val itemView: View =
            LayoutInflater.from(this).inflate(R.layout.layout_floating_translate, null)

      val  originalTXT = itemView.original_txt.text.toString()

        itemView.floating_translate_btn.setOnClickListener {
            translateTXT =  translateTxt(originalTXT)

        }

        itemView.floating_encrypt_btn.setOnClickListener {
            translateTXT = translateTxt(originalTXT)

        }

        itemView.translated_txt.text = translateTXT

//        Logger.d(TAG, "(showSettingDialog) will set this $translateTXT")

//
//        val popUp = PopupWindow(
//            itemView, LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT, false
//        )
//        popUp.isTouchable = true
//        popUp.isFocusable = true
//        popUp.isOutsideTouchable = true
//        popUp.showAsDropDown(image_collapsed)

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
              FLOATING_DIALOG_ACTION_START -> {
                  translateDialog.show()


              }
              FLOATING_DIALOG_ACTION_END -> {
                  translateDialog.dismiss()
//                  finishAfterTransition()

              }
          }
        translateDialog.setOnDismissListener {
            Logger.d(TAG, "showFloatingDialog ")
//            sendBroadcast(Intent(Constants.ENCRYPT_ACTION).putExtra("copied", true))

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

    private fun translateTxt(text: String): String {

        return translate.translateTxt(text)
    }
}