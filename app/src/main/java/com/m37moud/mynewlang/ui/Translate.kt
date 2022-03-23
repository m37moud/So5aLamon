package com.m37moud.mynewlang.ui

import TranslateMessageIMPL
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.m37moud.mynewlang.R
import android.app.AlertDialog
import android.view.*
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.m37moud.mynewlang.util.Constants.Companion.ORIGINAL_TXT
import com.m37moud.responsivestories.util.Logger
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.android.synthetic.main.layout_translate_app.view.*

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
        Logger.d(TAG, " action TEXT IS ${intent.action}")
        translate = TranslateMessageIMPL()


        if (!encryptTXT.isNullOrBlank()){
            translateTXT = translateTxt(encryptTXT)
            if (!translateTXT.isNullOrBlank()){
                showSettingDialog(encryptTXT, translateTXT)
            }

        }



    }

    override fun onStop() {
        super.onStop()
//        unregisterReceiver(notifyBroadcast)
    }

    override fun onBackPressed() {
      Toast.makeText(this@Translate , "اضغط حسنا للقفل",Toast.LENGTH_SHORT).show()

    }


    private fun showSettingDialog(encryptTXT: String, translateTXT: String) {

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
            finishAfterTransition()
        }


    }

    private fun translateTxt(text : String):String{

        return translate.translateTxt(text)
    }
}