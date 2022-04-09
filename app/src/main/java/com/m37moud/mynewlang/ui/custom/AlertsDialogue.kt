package com.m37moud.mynewlang.ui.custom

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Window
import android.view.WindowManager
import android.widget.Toast


class AlertsDialogue// the important stuff..
    (context: Context, title: String?, message: String?) {

    private var alertDialogBuilder: AlertDialog.Builder? = null
    private var alert: AlertDialog? = null

    init {
        alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder!!.setTitle(title)
        alertDialogBuilder!!.setIcon(R.drawable.ic_input_add)
        alertDialogBuilder!!.setMessage(message)
            .setCancelable(false)
        alert = alertDialogBuilder!!.create()
        val window: Window = alert!!.getWindow()!!
        if (window != null) {
            // the important stuff..
            window.setType(WindowManager.LayoutParams.TYPE_TOAST)
            alert!!.getWindow()!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert!!.show()
        } else Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}