package mch.helloworld

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyReceiver : BroadcastReceiver() {

    val TAG = "MyReceiver"

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "onReceive: ")

        context.startService(Intent(context, MyService::class.java))
    }
}
