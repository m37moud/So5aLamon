package com.m37moud.mynewlang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import com.m37moud.responsivestories.util.Logger
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var animationStarted = false
    val STARTUP_DELAY = 300
    val ANIM_ITEM_DURATION = 1000
    val ITEM_DELAY = 300

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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus || animationStarted) {
            return
        }

        animate();
        super.onWindowFocusChanged(hasFocus)
    }


    private fun animate() {
        ViewCompat.animate(img_logo)
            .translationY(-250f)
            .setStartDelay(STARTUP_DELAY.toLong())
            .setDuration(ANIM_ITEM_DURATION.toLong()).setInterpolator(
                DecelerateInterpolator(1.2f)

            )
            .start()

        container.visibility = View.VISIBLE
        for (i in 0 until container.childCount) {
            val v = container.getChildAt(i)
            var viewAnimator: ViewPropertyAnimatorCompat = if (v !is Button) {
                ViewCompat.animate(v)
                    .translationY(50f).alpha(1f)
                    .setStartDelay((ITEM_DELAY * i + 500).toLong())
                    .setDuration(1000)
            } else {
                ViewCompat.animate(v)
                    .scaleY(1f).scaleX(1f)
                    .setStartDelay((ITEM_DELAY * i + 500).toLong())
                    .setDuration(500)
            }
            viewAnimator.setInterpolator(DecelerateInterpolator()).start()
        }
    }
}