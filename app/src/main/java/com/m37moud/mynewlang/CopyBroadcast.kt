package com.m37moud.mynewlang

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.m37moud.responsivestories.util.Logger

private const val TAG = "CopyBroadcast"

class CopyBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.e(TAG, "start service")

//        if (intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
//            val service: ComponentName = context!!.startService(
//                Intent(context, ClipboardService::class.java)
//            )!!
//            Logger.d(TAG, "start service")
//            if (service == null) {
//                Logger.d(TAG, "Can't start service")
//            }
//        } else {
//            Logger.e(TAG, "Recieved unexpected intent $intent")
//        }
    }

}