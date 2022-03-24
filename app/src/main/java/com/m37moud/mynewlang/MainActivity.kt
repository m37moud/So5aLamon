package com.m37moud.mynewlang

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.responsivestories.util.Logger
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        Logger.d(TAG, "onCreate service start")
//
//        startForegroundService(
//            Intent(this@MainActivity, ClipboardService::class.java)
//        )
        launch_btn.setOnClickListener {
            startService(
                Intent(this@MainActivity, ClipboardService::class.java)
            )
            finish()
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           startForegroundService(Intent(this, ClipboardService::class.java))
//        } else {
//           startService(Intent(this, ClipboardService::class.java))
//        }
    }
}