package mch.helloworld

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import java.util.Timer

class MyService : Service() {

    val TAG = "MyService"

    var mBinder = MyBinder()

    val mTimer = Timer()

    inner class MyBinder : Binder() {
        val service = this@MyService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: 1")

        mTimer.scheduleAtFixedRate(Utilities.MyTask("service 1"), 0, 1000)

        startForeground(1000, Utilities.createNotification2(applicationContext))

        Handler().postDelayed({
            stopForeground(false)
            stopSelf()
        }, 10000)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        startForeground(1001, Utilities.createNotification(applicationContext))

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: 1")

        mTimer.cancel()
    }
}
