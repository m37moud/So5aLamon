package com.m37moud.mynewlang.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.m37moud.mynewlang.data.EncryptionMessageIMPL
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Constants.Companion.ENCRYPT_ACTION
import com.m37moud.mynewlang.util.Constants.Companion.textContainsArabic
import com.m37moud.mynewlang.util.Logger


private const val TAG = "EncryptionMessage"

class EncryptionMessage : AppCompatActivity() {
    private lateinit var encryption: EncryptionMessageIMPL
    private var selectedText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        NotificationManagerCompat.from(this).cancel(1001)

//        setContentView(R.layout.activity_encryption_message)

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
                Logger.d(TAG, "TEXT IS $selectedText")

                if (!textContainsArabic(selectedText)) {
                    Toast.makeText(this, "عربى بس", Toast.LENGTH_SHORT).show()
                    finish()
                } else {

                    val encryptedTxt = encryptTxt(selectedText)
                    Logger.d(TAG, "TEXT IS $encryptedTxt")


                    // textCountTv.text = "$selectedText \n\n  ${encryptedTxt}"


                    val intent = Intent()
                    intent.putExtra(Intent.EXTRA_PROCESS_TEXT, encryptedTxt)
                    setResult(RESULT_OK, intent)
                    Toast.makeText(
                        this,
                        "Text Length is:( ${encryptedTxt} ) ---isReadOnly= $isReadOnly",
                        Toast.LENGTH_LONG
                    ).show()
                    // Close activity after showing toast.
                    finish()
                }


            } else {
                val translateIntent = Intent(this, Translate::class.java).apply {
                    this.action = Constants.FLOATING_DIALOG_ACTION_START
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(translateIntent)

            }

        } else if (intent.action == Constants.ACTION_ENCRYPT) {


            val intent = intent
            selectedText = intent?.getStringExtra(Constants.ORIGINAL_TXT).toString()

            if (!textContainsArabic(selectedText)) {
                Toast.makeText(this, "عربى بس", Toast.LENGTH_SHORT).show()
                finish()

            } else {
                Logger.d(TAG, "TEXT IS $selectedText")

                val encryptedTxt = encryptTxt(selectedText)

                // try to remove clipboard listener from the service

                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("toPaste", encryptedTxt)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(this, "اضغط لصق لوضع النص الجديد بعد التغير", Toast.LENGTH_LONG)
                    .show()
                finish()

            }


        }
    }

    private fun encryptTxt(txt: String): String {
        return encryption.encryptTxt(txt)
    }
}