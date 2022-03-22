package com.m37moud.mynewlang

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.m37moud.responsivestories.util.Logger




private const val TAG = "NotifyBroadcast"


class NotifyBroadcast : BroadcastReceiver() {
    private val ACTION_TRANSLATE = "translate"
    private val ACTION_ENCRYPT = "encrypt"
    private val ORIGINAL_TXT = "originalTxt"


    @SuppressLint("ShowToast")
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val t = intent?.getStringExtra(ORIGINAL_TXT)
        Logger.d(TAG, "onReceive: action is = $action")
        if (action == ACTION_TRANSLATE) {
            //do required

            Toast.makeText(context, "  ${ACTION_TRANSLATE}", Toast.LENGTH_LONG)
            Logger.d(TAG, "if: aaaaaa is = $t")


//            clearService()
        } else if (action == ACTION_ENCRYPT) {
            //do required
            Toast.makeText(context, " ${ACTION_ENCRYPT}", Toast.LENGTH_LONG)
            Logger.d(TAG, "if: action is = $action")


//        clearService()
        }
    }
    private fun pasteEncrypt(){

    }

    private fun showTranslateTxt(){

    }
}
