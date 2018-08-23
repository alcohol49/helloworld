package mch.helloworld

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import java.util.*


class MyService2 : Service() {

    val TAG = "MyService2"

    companion object {
        fun stopService(): Intent {
            val intent = Intent().apply {
                component = ComponentName("mch.helloworld", "mch.helloworld.MyService2")
                putExtra("stop", true)
            }
            return intent
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: 2")

        startForeground(8, Utilities.createNotification2(applicationContext))

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(8, Utilities.createNotification2(applicationContext))

        Timer().scheduleAtFixedRate(Utilities.MyTask("service 2"), 0, 1000)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand:")
        if (intent.getBooleanExtra("stop", false)) stopSelf()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: 2")
    }
}
