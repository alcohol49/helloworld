package mch.helloworld

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import mch.helloworld.MyService2.Companion.stopService
import java.util.*

object Utilities {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("general", "general", NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(
                    Uri.parse("android.resource://${context.packageName}/${R.raw.audio_mode_alarm_clock}"),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT)

            val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getNotificationChannel(context: Context, channelId: String): NotificationChannel {
        val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager
        return notificationManager.getNotificationChannel(channelId)
    }

    fun createNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, "general")
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle("Hello World")
                .setContentText("text")
                .setSound(Uri.parse("android.resource://mch.helloworld/R.raw.audio_mode_alarm_clock"))
                .build()
    }

    fun createNotification2(context: Context): Notification {
        return NotificationCompat.Builder(context, "general")
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle("title 2")
                .setContentText("text")
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_action_name))
                .setContentIntent(PendingIntent.getService(context, 0, stopService(), 0))
                .addAction(R.drawable.ic_stat_notification, "title", PendingIntent.getService(context, 0, stopService(), 0))
                .build()
    }

    class MyTask(tag: String) : TimerTask() {

        var count = 0
        var mTag = tag

        override fun run() {
            count++
            Log.d("mch", "${mTag} run $count")
        }
    }
}