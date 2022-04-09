package com.m37moud.mynewlang.ui.custom

import android.content.DialogInterface
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.mynewlang.R


class MyAlertDialog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide activity title
        setContentView(R.layout.activity_my_alert_dialog)
        val Builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setMessage("Do You Want continue ?")
            .setTitle("exit")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton("no",
                DialogInterface.OnClickListener { dialog, which -> finish() })
            .setPositiveButton("yes", null)
        val alertDialog: AlertDialog = Builder.create()
        alertDialog.show()
    }
}