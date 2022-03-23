package com.m37moud.mynewlang

import android.R
import android.app.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Constants.Companion.ACTION_ENCRYPT
import com.m37moud.mynewlang.util.Constants.Companion.ACTION_TRANSLATE
import com.m37moud.mynewlang.util.Constants.Companion.ENCRYPRAT_TXT
import com.m37moud.mynewlang.util.Constants.Companion.ORIGINAL_TXT
import com.m37moud.responsivestories.util.Logger

private const val TAG = "ClipboardService"

class ClipboardService : Service() {


    private var txt = ""

    private var mClipboardManager: ClipboardManager? = null
    private val mOnPrimaryClipChangedListener: ClipboardManager.OnPrimaryClipChangedListener =
        ClipboardManager.OnPrimaryClipChangedListener {
            NotificationManagerCompat.from(applicationContext).cancel(1001)

            Logger.d(TAG, "onPrimaryClipChanged")
            val clip: ClipData = mClipboardManager!!.primaryClip!!
            txt = (clip.getItemAt(0).text).toString()
//            Toast.makeText(applicationContext, " text clip inserted:: ${txt}", Toast.LENGTH_LONG).show()


            showNotification(applicationContext)
            Logger.d(TAG, "new text clip inserted: " + txt.toString())
            //                mThreadPool.execute(
            //                    WriteHistoryRunnable(
            //
            //                    )
            //                )
        }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        mClipboardManager!!.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener)
        Logger.d(TAG, "onCreate service start")

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mClipboardManager != null) {
            mClipboardManager!!.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener)

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun showNotification(context: Context) {
        Logger.d(TAG, "(showNotification) called")
//
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId: String = createExoDownloadNotificationChannel(context)!!
//        val notificationID = Random.nextInt()

        val translate = Intent(this, NotifyBroadcast::class.java).apply {
            action = ACTION_TRANSLATE
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            putExtra(EXTRA_NOTIFICATION_ID, notificationID)
            putExtra(ORIGINAL_TXT, txt)

        }
        val translateIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, translate, PendingIntent.FLAG_UPDATE_CURRENT)
        //**************************************************
        val encrypte = Intent(this, NotifyBroadcast::class.java).apply {
            action = ACTION_ENCRYPT
            putExtra(ORIGINAL_TXT, txt)

//            putExtra(EXTRA_NOTIFICATION_ID, notificationID)

        }
        val encrypteIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, encrypte, PendingIntent.FLAG_UPDATE_CURRENT)
        //**************************************************

        val alarmSound: Uri = RingtoneManager.getDefaultUri(TYPE_NOTIFICATION)
        val notification: Notification =
            NotificationCompat.Builder(context, channelId)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.holo_blue_bright))
                .setContentTitle("Sa5a Lamon")
                .setContentText("select action")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.sym_def_app_icon)
                .setWhen(System.currentTimeMillis())
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(
                    R.drawable.ic_media_play,
                    "translate",
                    translateIntent
                )
                .addAction(
                    R.drawable.sym_def_app_icon,
                    "encryptIntent",
                    encrypteIntent
                )
                .build()
        notificationManager.notify(1001, notification)
//        startForeground(1001, notification)

//        with(NotificationManagerCompat.from(application)) {
//            // notificationId is a unique int for each notification that you must define
//            notify(1001, notification)
//        }

        //******************
//        Logger.d(TAG, "( createNotification ) notify called")


//        val notificationCompleted =
//            NotificationCompat.Builder(context, channelId)
//                .setColor(ContextCompat.getColor(context, R.color.background_dark))
//                .setContentTitle(model.title)
//                .setContentText("new story is added")
//                .setAutoCancel(false)
//                .setWhen(System.currentTimeMillis())
//                .setOnlyAlertOnce(true)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build()
//        notificationManager.notify(1003, notificationCompleted)
        //********************
    }

    private fun createExoDownloadNotificationChannel(context: Context): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "1017"
            val channelName: CharSequence = "sa5a lamon"
            val channelDescription = "sa5a lamon lang"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH
//                        val channelEnableVibrate = true
//                        val channelLockscreenVisibility = Notification.VISIBILITY_SECRET

            // Initializes NotificationChannel.
            val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
            notificationChannel.description = channelDescription
//            notificationChannel.enableVibration(false)
//            notificationChannel.lockscreenVisibility = channelLockscreenVisibility

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            val notificationManager =
                (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            notificationManager.createNotificationChannel(notificationChannel)
            channelId
        } else {
            // Returns null for pre-O (26) devices.
            null
        }
    }

}