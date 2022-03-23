package com.m37moud.mynewlang

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.responsivestories.util.Logger

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.d(TAG, "onCreate service start")
//
        startService(
            Intent(this@MainActivity, ClipboardService::class.java)
        )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           startForegroundService(Intent(this, ClipboardService::class.java))
//        } else {
//           startService(Intent(this, ClipboardService::class.java))
//        }
    }
}