package com.m37moud.mynewlang

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.view.*
import android.widget.ImageView

class FloatingWidgetService : Service(), View.OnClickListener {
    private var mWindowManager: WindowManager? = null
    private var mFloatingWidgetView: View? = null
    private var collapsedView: View? = null
    private var expandedView: View? = null
    private var remove_image_view: ImageView? = null
    private val szWindow = Point()
    private var removeFloatingWidgetView: View? = null
    private var x_init_cord = 0
    private var y_init_cord = 0
    private var x_init_margin = 0
    private var y_init_margin = 0

    private var isLeft = true
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManagerDefaultDisplay

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addRemoveView(inflater)
        addFloatingWidgetView(inflater)
        implementClickListeners()
        implementTouchListenerToFloatingWidgetView()
    }

    private fun addRemoveView(inflater: LayoutInflater): View? {
        removeFloatingWidgetView = inflater.inflate(R.layout.remove_floating_layout, null)

        val paramRemove: WindowManager.LayoutParams
        paramRemove = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        }

        paramRemove.gravity = Gravity.TOP or Gravity.LEFT

        removeFloatingWidgetView?.setVisibility(View.GONE)
        remove_image_view = removeFloatingWidgetView?.findViewById<View>(R.id.image_remove) as ImageView

        mWindowManager!!.addView(removeFloatingWidgetView, paramRemove)
        return remove_image_view
    }

    private fun addFloatingWidgetView(inflater: LayoutInflater) {
        //Inflate the floating view layout we created
        mFloatingWidgetView = inflater.inflate(R.layout.floating_widget_layout, null)

        val params: WindowManager.LayoutParams
        params = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        }


        params.gravity = Gravity.TOP or Gravity.LEFT

        params.x = 0
        params.y = 100

        mWindowManager!!.addView(mFloatingWidgetView, params)

        collapsedView = mFloatingWidgetView?.findViewById(R.id.image_collapsed)

        expandedView = mFloatingWidgetView?.findViewById(R.id.linear_expanded)
    }

    private val windowManagerDefaultDisplay: Unit
        private get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) mWindowManager!!.defaultDisplay.getSize(szWindow) else {
                val w = mWindowManager!!.defaultDisplay.width
                val h = mWindowManager!!.defaultDisplay.height
                szWindow[w] = h
            }
        }

    private fun implementTouchListenerToFloatingWidgetView() {
        mFloatingWidgetView!!.findViewById<View>(R.id.root_layout).setOnTouchListener(object :
            View.OnTouchListener {
            var time_start: Long = 0
            var time_end: Long = 0
            var isLongClick = false
            var inBounded = false
            var remove_img_width = 0
            var remove_img_height = 0
            var handler_longClick = Handler()
            var runnable_longClick = Runnable {

                isLongClick = true

                removeFloatingWidgetView!!.visibility = View.VISIBLE
                onFloatingWidgetLongClick()
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                val layoutParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams

                val x_cord = event.rawX.toInt()
                val y_cord = event.rawY.toInt()
                val x_cord_Destination: Int
                var y_cord_Destination: Int
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        time_start = System.currentTimeMillis()
                        handler_longClick.postDelayed(runnable_longClick, 600)
                        remove_img_width = remove_image_view!!.layoutParams.width
                        remove_img_height = remove_image_view!!.layoutParams.height
                        x_init_cord = x_cord
                        y_init_cord = y_cord

                        x_init_margin = layoutParams.x
                        y_init_margin = layoutParams.y
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        isLongClick = false
                        removeFloatingWidgetView!!.visibility = View.GONE
                        remove_image_view!!.layoutParams.height = remove_img_height
                        remove_image_view!!.layoutParams.width = remove_img_width
                        handler_longClick.removeCallbacks(runnable_longClick)

                        if (inBounded) {
                            stopSelf()
                            inBounded = false
//                             break
                        }


                        val x_diff = x_cord - x_init_cord
                        val y_diff = y_cord - y_init_cord


                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis()

                            if (time_end - time_start < 300) onFloatingWidgetClick()
                        }
                        y_cord_Destination = y_init_margin + y_diff
                        val barHeight = statusBarHeight
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0
                        } else if (y_cord_Destination + (mFloatingWidgetView!!.height + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (mFloatingWidgetView!!.height + barHeight)
                        }
                        layoutParams.y = y_cord_Destination
                        inBounded = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val x_diff_move = x_cord - x_init_cord
                        val y_diff_move = y_cord - y_init_cord
                        x_cord_Destination = x_init_margin + x_diff_move
                        y_cord_Destination = y_init_margin + y_diff_move

                        if (isLongClick) {
                            val x_bound_left = szWindow.x / 2 - (remove_img_width * 1.5).toInt()
                            val x_bound_right = szWindow.x / 2 + (remove_img_width * 1.5).toInt()
                            val y_bound_top = szWindow.y - (remove_img_height * 1.5).toInt()

                            if (x_cord >= x_bound_left && x_cord <= x_bound_right && y_cord >= y_bound_top) {
                                inBounded = true
                                val x_cord_remove = ((szWindow.x - remove_img_height * 1.5) / 2).toInt()
                                val y_cord_remove = (szWindow.y - (remove_img_width * 1.5 + statusBarHeight)).toInt()
                                if (remove_image_view!!.layoutParams.height == remove_img_height) {
                                    remove_image_view!!.layoutParams.height = (remove_img_height * 1.5).toInt()
                                    remove_image_view!!.layoutParams.width = (remove_img_width * 1.5).toInt()
                                    val param_remove = removeFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
                                    param_remove.x = x_cord_remove
                                    param_remove.y = y_cord_remove
                                    mWindowManager!!.updateViewLayout(removeFloatingWidgetView, param_remove)
                                }
                                layoutParams.x = x_cord_remove + Math.abs(removeFloatingWidgetView!!.width - mFloatingWidgetView!!.width) / 2
                                layoutParams.y = y_cord_remove + Math.abs(removeFloatingWidgetView!!.height - mFloatingWidgetView!!.height) / 2

                                mWindowManager!!.updateViewLayout(mFloatingWidgetView, layoutParams)
//                                break
                            } else {
                                inBounded = false
                                remove_image_view!!.layoutParams.height = remove_img_height
                                remove_image_view!!.layoutParams.width = remove_img_width
                                onFloatingWidgetClick()
                            }
                        }
                        layoutParams.x = x_cord_Destination
                        layoutParams.y = y_cord_Destination

                        mWindowManager!!.updateViewLayout(mFloatingWidgetView, layoutParams)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun implementClickListeners() {
        mFloatingWidgetView!!.findViewById<View>(R.id.image_close_float).setOnClickListener(this)
        mFloatingWidgetView!!.findViewById<View>(R.id.linear_expanded).setOnClickListener(this)
        mFloatingWidgetView!!.findViewById<View>(R.id.image_open).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_close_float ->
                stopSelf()
            R.id.image_close_float -> {
                collapsedView!!.visibility = View.VISIBLE
                expandedView!!.visibility = View.GONE
            }
            R.id.image_open
            -> {
                val intent = Intent(this@FloatingWidgetService, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                stopSelf()
            }
        }
    }

    private fun onFloatingWidgetLongClick() {
        val removeParams = removeFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams

        val x_cord = (szWindow.x - removeFloatingWidgetView!!.width) / 2
        val y_cord = szWindow.y - (removeFloatingWidgetView!!.height + statusBarHeight)
        removeParams.x = x_cord
        removeParams.y = y_cord

        mWindowManager!!.updateViewLayout(removeFloatingWidgetView, removeParams)
    }

    private fun resetPosition(x_cord_now: Int) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true
            moveToLeft(x_cord_now)
        } else {
            isLeft = false
            moveToRight(x_cord_now)
        }
    }

    private fun moveToLeft(current_x_cord: Int) {
        val x = szWindow.x - current_x_cord
        object : CountDownTimer(500, 5) {
            var mParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams.x = 0 - (current_x_cord * current_x_cord * step).toInt()


                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0

                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }
        }.start()
    }

    private fun moveToRight(current_x_cord: Int) {
        object : CountDownTimer(500, 5) {
            var mParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams.x = (szWindow.x + current_x_cord * current_x_cord * step - mFloatingWidgetView!!.width).toInt()

                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams.x = szWindow.x - mFloatingWidgetView!!.width

                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }
        }.start()
    }

    private fun bounceValue(step: Long, scale: Long): Double {
        return scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step)
    }

    private val isViewCollapsed: Boolean
        private get() = mFloatingWidgetView == null || mFloatingWidgetView!!.findViewById<View>(R.id.image_collapsed).visibility == View.VISIBLE


    private val statusBarHeight: Int
        private get() = Math.ceil(25 * applicationContext.resources.displayMetrics.density.toDouble()).toInt()


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        windowManagerDefaultDisplay
        val layoutParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (layoutParams.y + (mFloatingWidgetView!!.height + statusBarHeight) > szWindow.y) {
                layoutParams.y = szWindow.y - (mFloatingWidgetView!!.height + statusBarHeight)
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, layoutParams)
            }
            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x)
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x)
            }
        }
    }


    private fun onFloatingWidgetClick() {
        if (isViewCollapsed) {

            collapsedView!!.visibility = View.GONE
            expandedView!!.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (removeFloatingWidgetView != null) mWindowManager!!.removeView(removeFloatingWidgetView)
    }
}

