package com.m37moud.mynewlang

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.m37moud.mynewlang.ui.Translate
import com.m37moud.mynewlang.util.Constants
import com.m37moud.responsivestories.util.Logger




private const val TAG = "NotifyBroadcast"


class NotifyBroadcast : BroadcastReceiver() {
    private val ACTION_TRANSLATE = "translate"
    private val ACTION_ENCRYPT = "encrypt"
    private val ORIGINAL_TXT = "originalTxt"


    @SuppressLint("ShowToast")
    override fun onReceive(context: Context?, intent: Intent?) {
        NotificationManagerCompat.from(context!!).cancel(1001)

        val action = intent?.action
        Logger.d(TAG, "onReceive: action is = $action")
        if (action == ACTION_TRANSLATE) {

            //do required
            val t = intent.getStringExtra(ORIGINAL_TXT)
           val translateIntent = Intent(context , Translate::class.java).apply {
                putExtra(ORIGINAL_TXT,t)
               flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context?.startActivity(translateIntent)


            Toast.makeText(context, "  ${ACTION_TRANSLATE}", Toast.LENGTH_LONG)
            Logger.d(TAG, "if: aaaaaa is = $t")


//            clearService()
        } else if (action == ACTION_ENCRYPT) {
            //do required

            val t = intent.getStringExtra(ORIGINAL_TXT)
            val encryptIntent = Intent(context , EncryptionMessage::class.java).apply {
                putExtra(ORIGINAL_TXT,t)
                this.action = Constants.ACTION_ENCRYPT
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context?.startActivity(encryptIntent)

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
