package com.m37moud.mynewlang

import android.app.*
import android.content.*
import android.graphics.Color
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.net.Uri
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.m37moud.mynewlang.util.Constants
import com.m37moud.mynewlang.util.Constants.Companion.ACTION_ENCRYPT
import com.m37moud.mynewlang.util.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.m37moud.mynewlang.util.Constants.Companion.ACTION_STOP_SERVICE
import com.m37moud.mynewlang.util.Constants.Companion.ACTION_TRANSLATE
import com.m37moud.mynewlang.util.Constants.Companion.ENCRYPT_ACTION
import com.m37moud.mynewlang.util.Constants.Companion.ORIGINAL_TXT
import com.m37moud.mynewlang.util.Constants.Companion.textContainsArabic
import com.m37moud.mynewlang.util.Logger

private const val TAG = "ClipboardService"

class ClipboardService : Service() {


    private var txt = ""
    private var encyprate = false
    var copiedState = false
    private var isServiceStarted = false

    private var mClipboardManager: ClipboardManager? = null
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
             copiedState = intent.getBooleanExtra("copied", false)
            Logger.d(TAG,"onReceive: ${intent.getBooleanExtra("copied", true)}")

            if(copiedState) encyprate = false
        }
    }
//    private val mOnPrimaryClipChangedListener: ClipboardManager.OnPrimaryClipChangedListener =
//        ClipboardManager.OnPrimaryClipChangedListener {
//            NotificationManagerCompat.from(applicationContext).cancel(1001)
//
//            Logger.d(TAG, "onPrimaryClipChanged")
//            val clip: ClipData = mClipboardManager!!.primaryClip!!
//            txt = (clip.getItemAt(0).text).toString()
////            Toast.makeText(applicationContext, " text clip inserted:: ${txt}", Toast.LENGTH_LONG).show()
//
//
////           if(!encyprate)
//            showNotification(applicationContext)
//
//            Logger.d(TAG, "new text clip inserted: " + txt.toString())
//        }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Logger.d(TAG, "onCreate called and encyprate is $encyprate ")

        mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//        mClipboardManager!!.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener)
//        LocalBroadcastManager.getInstance(this).
        registerReceiver(broadcastReceiver, IntentFilter(ENCRYPT_ACTION))
        mClipboardManager!!.addPrimaryClipChangedListener {


            NotificationManagerCompat.from(applicationContext).cancel(1001)

            Logger.d(TAG, "onPrimaryClipChanged encyprate is $encyprate - and copiedState = $copiedState")
            val clip: ClipData = mClipboardManager!!.primaryClip!!
            txt = (clip.getItemAt(0).text).toString()
//            Toast.makeText(applicationContext, " text clip inserted:: ${txt}", Toast.LENGTH_LONG).show()
            Logger.d(TAG, "new text clip inserted: " + txt.toString())

            if(!textContainsArabic(txt)) {
                Toast.makeText(this, "عربى بس", Toast.LENGTH_SHORT).show()

            }else{
                if(!encyprate) {
                    showNotification(applicationContext)
                }
                else{
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            encyprate =false


                        }, 1000
                    )
                }
            }



        }



        Logger.d(TAG, "onCreate service start")


    }

    override fun onDestroy() {
        Logger.d(TAG, "onDestroy called ")

        super.onDestroy()
//        stopService()
        unregisterReceiver(broadcastReceiver)
        if (mClipboardManager != null) {
//            mClipboardManager!!.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener)

        }
        NotificationManagerCompat.from(applicationContext).cancel(1001)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d(TAG, "onStartCommand called ")

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    var notification = serviceNotification()
                    startForeground(1, notification)
                }
                ACTION_STOP_SERVICE -> {
                    stopService()
                }

            }

        }



        return START_STICKY
    }

        override fun onTaskRemoved(rootIntent: Intent?) {
        Logger.d(TAG, "onTaskRemoved called ")

        val restartServiceIntent = Intent(applicationContext, this::class.java)
        restartServiceIntent.setPackage(packageName)
        val restartServicePendingIntent = PendingIntent.getService(applicationContext,1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT)

        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent)
//        super.onTaskRemoved(rootIntent)
    }
    private fun stopService() {
        Logger.d(TAG, "stopService called ")

        Logger.d("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {

            stopForeground(true)
            stopSelf()
            NotificationManagerCompat.from(applicationContext).cancel(1001)

        } catch (e: Exception) {
            Logger.d("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
//        setServiceState(this, ServiceState.STOPPED)
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
            encyprate = true

//            putExtra(EXTRA_NOTIFICATION_ID, notificationID)

        }
        val encrypteIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, encrypte, PendingIntent.FLAG_UPDATE_CURRENT)
        //**************************************************

        val alarmSound: Uri = RingtoneManager.getDefaultUri(TYPE_NOTIFICATION)
        val notification: Notification =
            NotificationCompat.Builder(context, channelId)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.orange))
                .setContentTitle("Sa5a Lamon")
                .setContentText("select action")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setSound(alarmSound)
                .setGroup("GROUP")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(
                    R.mipmap.ic_launcher,
                    "translate",
                    translateIntent
                )
                .addAction(
                    R.mipmap.ic_launcher,
                    "encryptIntent",
                    encrypteIntent
                )
                .build()
        notificationManager.notify(1001, notification)
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


    private fun serviceNotification(): Notification {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;

            val channel = NotificationChannel(
                notificationChannelId,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_LOW
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
//                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, ClipboardService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }.let { notificationIntent ->
            PendingIntent.getService(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
            ) else Notification.Builder(this)

        return builder
            .setAutoCancel(false) // to make user cant dismiss notification
            .setContentTitle("سغة لمون")
            .setContentText("سغال شجرة")
            .addAction(R.drawable.ic_happy, "STOP", pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true) // to make notification cant swipe away
            .setPriority(Notification.PRIORITY_LOW) // for under android 26 compatibility
            .build()
    }

}