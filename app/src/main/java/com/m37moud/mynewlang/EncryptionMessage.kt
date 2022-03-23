package com.m37moud.mynewlang

import android.content.ClipData
import android.content.ClipboardManager
import com.m37moud.mynewlang.data.EncryptionMessageIMPL
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Constants.Companion.ENCRYPRAT_TXT
import com.m37moud.responsivestories.util.Logger
private const val TAG = "EncryptionMessage"

class EncryptionMessage : AppCompatActivity() {
    private lateinit var encryption: EncryptionMessageIMPL
    private var selectedText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        NotificationManagerCompat.from(this).cancel(1001)

        setContentView(R.layout.activity_encryption_message)

		encryption = EncryptionMessageIMPL()

        val action = intent?.action
        Logger.d(TAG, "action is IS $action")


        // Check if the intent action is ACTION_PROCESS_TEXT
        if (intent.action == Intent.ACTION_PROCESS_TEXT) {
			 val isReadOnly = intent.getBooleanExtra(
                Intent.EXTRA_PROCESS_TEXT_READONLY, false
            )
			if (!isReadOnly) {
				// Get the text from the intent extra.
				 selectedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""

				val encryptedTxt = encryptTxt(selectedText)

				// textCountTv.text = "$selectedText \n\n  ${encryptedTxt}"
           
            
                val intent = Intent()
                intent.putExtra(Intent.EXTRA_PROCESS_TEXT, encryptedTxt)
                setResult(RESULT_OK, intent)
                Toast.makeText(this,"Text Length is: ${encryptedTxt} ---isReadOnly= $isReadOnly", Toast.LENGTH_LONG).show()
				// Close activity after showing toast.
                finish()
            }

        }
        else if(intent.action == Constants.ACTION_ENCRYPT){


            val intent = intent
            selectedText = intent?.getStringExtra(Constants.ORIGINAL_TXT).toString()
            Logger.d(TAG, "TEXT IS $selectedText")

            val encryptedTxt = encryptTxt(selectedText)

            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("toPaste" , encryptedTxt)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this,"اضغط لصق لوضع النص الجديد بعد التغير", Toast.LENGTH_LONG).show()

//            showSnackBar("اضغط لصق لوضع النص الجديد بعد التغير")
//            Snackbar.make(
//                view,
//                msg,
//                Snackbar.LENGTH_LONG
//            ).setAction(getString(R.string.OK)) {}
//                .show()
            finish()

        }
    }

    private fun encryptTxt(txt: String): String {
        return encryption.encryptTxt(txt)
    }
    private fun showSnackBar(msg: String) {
        val view = this.findViewById<View>(R.id.encrypt_txt_container)
        Snackbar.make(
            view,
            msg,
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.OK)) {}
            .show()
    }
}