package com.m37moud.mynewlang

import android.R
import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.m37moud.responsivestories.util.Logger

private const val TAG = "ClipboardMonitorService"
class ClipboardMonitorService : Service() {
    private val mTask= MonitorTask()
    private var mCM : ClipboardManager? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mCM = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        mTask.start()
        Logger.d(TAG, "onCreate service start")
    }

    override fun onDestroy() {
        Logger.d(TAG, "onDestroy service start")

        mTask.cancel()
        super.onDestroy()
    }

    fun showNotification(context: Context) {
        Logger.d(TAG, "(showNotification) called")

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId: String = createExoDownloadNotificationChannel(context)!!

//       Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


//        // Start/Resume Download
//        val pIntentStart = Intent(context, DemoDownloadService::class.java)
//        pIntentStart.action = Constants.EXO_DOWNLOAD_ACTION_START
//        pIntentStart.data = download.request.uri
//        val pendingIntentStart = PendingIntent.getService(this, 100, pIntentStart, 0)
        val notification: Notification =
            NotificationCompat.Builder(context, channelId)
                .setOngoing(true)
                .setAutoCancel(false)
                .setColor(ContextCompat.getColor(context, R.color.holo_blue_bright))
                .setContentTitle("So5a Lamon")
                .setContentText("select action")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.sym_def_app_icon)
                //.setGroup(GROUP_KEY_WORK_EMAIL)
//                .addAction(
//                    NotificationCompat.Action(
//                        R.drawable.ic_media_play,
//                        "Pause",
//                        action()
//                    )
//                )
                //.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Cancel", pendingIntentCancel))
                .build()
        startForeground(1001, notification)


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
            val channelName: CharSequence = "Adaptive Exo Download"
            val channelDescription = "Adaptive Exoplayer video Download"
            val channelImportance = NotificationManager.IMPORTANCE_NONE
            //            boolean channelEnableVibrate = true;
            //            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
            notificationChannel.description = channelDescription
            notificationChannel.enableVibration(false)
            //            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

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


    private fun action() {

    }


    inner class MonitorTask : Thread() {
        private var mKeepRunning = false
        private var mOldTxtClip: String? = null

         fun cancel(){
            mKeepRunning = false
            interrupt()
        }

        override fun run() {
            Logger.d(TAG, "(doTask) run")


            mKeepRunning = true
            while ( true){
                doTask()
                if (!mKeepRunning) {
                    break
                }
            }
        }
        private fun doTask(){
            Logger.d(TAG, "(doTask) called")

            if(mCM != null){
                Logger.d(TAG, "(doTask)mCM not null")

                val clip = mCM!!.primaryClip
//                val t = clip!!.getItemAt(0).text
//                if(mCM!!.hasText()){
//                    val newTxtClip = mCM!!.text.toString()
                try{
                    val newTxtClip = clip!!.getItemAt(0).text.toString()
                    if(newTxtClip != mOldTxtClip){
                        mOldTxtClip = newTxtClip
//                        Toast.makeText(applicationContext," text clip inserted:: ${newTxtClip}", Toast.LENGTH_LONG).show()
                        showNotification()
                        Logger.d("TAG", "new text clip inserted: " + newTxtClip.toString())
                    }

                }catch (e:Exception){
                    Logger.d("TAG", "new text clip inserted: " + e.toString())

                }

//                }
            }
            Logger.d(TAG, "(doTask) mCM is null")



        }
        private fun showNotification() {
            Logger.d(TAG, "(showNotification) called")

            val notificationManager =
                this@ClipboardMonitorService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId: String = createExoDownloadNotificationChannel(this@ClipboardMonitorService)!!

//       Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


//        // Start/Resume Download
//        val pIntentStart = Intent(context, DemoDownloadService::class.java)
//        pIntentStart.action = Constants.EXO_DOWNLOAD_ACTION_START
//        pIntentStart.data = download.request.uri
//        val pendingIntentStart = PendingIntent.getService(this, 100, pIntentStart, 0)
            val notification: Notification =
                NotificationCompat.Builder(this@ClipboardMonitorService, channelId)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setColor(ContextCompat.getColor(this@ClipboardMonitorService, R.color.holo_blue_bright))
                    .setContentTitle("So5a Lamon")
                    .setContentText("select action")
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(R.mipmap.sym_def_app_icon)
                    //.setGroup(GROUP_KEY_WORK_EMAIL)
//                .addAction(
//                    NotificationCompat.Action(
//                        R.drawable.ic_media_play,
//                        "Pause",
//                        action()
//                    )
//                )
                    //.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Cancel", pendingIntentCancel))
                    .build()
            startForeground(1001, notification)


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
                val channelName: CharSequence = "Adaptive Exo Download"
                val channelDescription = "Adaptive Exoplayer video Download"
                val channelImportance = NotificationManager.IMPORTANCE_NONE
                //            boolean channelEnableVibrate = true;
                //            int channelLockscreenVisibility = Notification.;

                // Initializes NotificationChannel.
                val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
                notificationChannel.description = channelDescription
                notificationChannel.enableVibration(false)
                //            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

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

}