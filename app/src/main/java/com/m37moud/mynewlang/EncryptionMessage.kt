package com.m37moud.mynewlang

import EncryptionMessageIMPL
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_encryption_message.*

class EncryptionMessage : AppCompatActivity() {
    private lateinit var encryption: EncryptionMessageIMPL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encryption_message)
		encryption = EncryptionMessageIMPL()
		
        // Check if the intent action is ACTION_PROCESS_TEXT
        if (intent.action == Intent.ACTION_PROCESS_TEXT) {
			 val isReadOnly = intent.getBooleanExtra(
                Intent.EXTRA_PROCESS_TEXT_READONLY, false
            )
			if (!isReadOnly) {
				// Get the text from the intent extra.
				val selectedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""

				val encryptedTxt = encryptTxt(selectedText)

				// textCountTv.text = "$selectedText \n\n  ${encryptedTxt}"
           
            
                val intent = Intent()
                intent.putExtra(Intent.EXTRA_PROCESS_TEXT, encryptedTxt)
                setResult(RESULT_OK, intent)
                Toast.makeText(this,"Text Length is: ${encryptedTxt} ---isReadOnly= $isReadOnly", Toast.LENGTH_LONG).show()
				// Close activity after showing toast.
//                finish()
            }

        }
    }

    private fun encryptTxt(txt: String): String {
        return encryption.encryptTxt(txt)
    }
}