package com.m37moud.mynewlang.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.m37moud.mynewlang.R
import android.app.AlertDialog
import android.content.Intent
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.m37moud.responsivestories.util.Logger
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.android.synthetic.main.layout_translate_app.view.*

private const val TAG = "Translate"

class Translate : AppCompatActivity() {
    private val ORIGINAL_TXT = "originalTxt"
    private var txt = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)
        val intent = Intent()
        txt = intent?.getStringExtra(ORIGINAL_TXT).toString()
        Logger.d(TAG, " onCreate TEXT IS $txt")

        showSettingDialog()
    }


    private fun showSettingDialog() {

        val builder = AlertDialog.Builder(this)

        val itemView: View = LayoutInflater.from(this).inflate(R.layout.layout_translate_app, null)

        itemView.original_txt.text = txt

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
        val settingsDialog = builder.create()
        settingsDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = settingsDialog.window
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.windowAnimations = R.style.DialogAnimation

//        settingsDialog.setCancelable(false)
//        settingsDialog.setCanceledOnTouchOutside(false)
        itemView.okay_btn.setOnClickListener {
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {

//            if(itemView.play_sound_setting.isVisible)


                }.doAction()


        }



        settingsDialog.show()
        settingsDialog.setOnDismissListener {

        }


    }
}