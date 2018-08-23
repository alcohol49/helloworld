package mch.helloworld

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ListView
import mch.helloworld.dummy.DummyContent
import java.io.File

class MainActivity : AppCompatActivity(), ItemListDialogFragment.Listener, ItemFragment.OnListFragmentInteractionListener, BlankFragment.OnFragmentInteractionListener {

    val TAG = "MainActivity"

    var mReceiver = MyReceiver()

    var mService: MyService? = null

    var mBound = false

    var mListView: ListView? = null

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(cName: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")

            val binder = service as MyService.MyBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(cName: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")

            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setContentView(R.layout.activity_main)

        Utilities.createNotificationChannel(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Utilities.getNotificationChannel(applicationContext, "general").run {
                Log.d(TAG, "getNotificationChannel $this")
            }
        }

        registerReceiver(mReceiver, IntentFilter("hello"))

        Log.d(TAG, "DEVICE_NAME ${Settings.Global.getString(contentResolver, Settings.Global.DEVICE_NAME)}")
        Log.d(TAG, "MANUFACTURER ${Build.MANUFACTURER}")
        Log.d(TAG, "PRODUCT ${Build.PRODUCT}")
        Log.d(TAG, "DEVICE ${Build.DEVICE}")
        Log.d(TAG, "MODEL ${Build.MODEL}")


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
//            RingtoneManager(this).run {
//                setType(RingtoneManager.TYPE_NOTIFICATION)
//                Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor))
//            }


            val uri = MediaStore.Audio.Media.getContentUriForPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).path)
            val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.TITLE)
            val selection = "${MediaStore.MediaColumns.TITLE} LIKE ?"
            val selectionArgs = arrayOf("[Lollipop]%")

            val paths = ArrayList<String>()

            contentResolver.query(uri, projection, selection, selectionArgs, null).use {
                Log.d(TAG, DatabaseUtils.dumpCursorToString(it))
                while (it.moveToNext()) {
                    paths.add(it.getString(it.getColumnIndex(MediaStore.MediaColumns.DATA)))
                }
            }

            paths.forEach {
                File(it).delete()
            }

            contentResolver.delete(uri, "${MediaStore.MediaColumns.TITLE} LIKE ?", selectionArgs)

        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")

        Handler().postDelayed({ SimpleJobIntentService.enqueueWork(applicationContext, Intent().putExtra("label", "1 min")) }, 1000 * 60)

        Handler().postDelayed({ SimpleJobIntentService.enqueueWork(applicationContext, Intent().putExtra("label", "2 min")) }, 1000 * 60 * 2)
//        Handler().postDelayed({ sendBroadcast(Intent("hello")) }, 1000 * 60 * 2)

        Handler().postDelayed({ SimpleJobIntentService.enqueueWork(applicationContext, Intent().putExtra("label", "3 min")) }, 1000 * 60 * 3)

        Handler().postDelayed({ SimpleJobIntentService.enqueueWork(applicationContext, Intent().putExtra("label", "4 min")) }, 1000 * 60 * 4)

        Handler().postDelayed({ SimpleJobIntentService.enqueueWork(applicationContext, Intent().putExtra("label", "5 min")) }, 1000 * 60 * 5)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(mReceiver)
    }

    override fun onItemClicked(position: Int) {
        Log.d(TAG, position.toString())
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {

    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onStartService(view: View) {
        startService(Intent(applicationContext, MyService::class.java))
//        startService(Intent(applicationContext, MyService2::class.java))
    }

    fun onSendNotification(view: View) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(999, Utilities.createNotification(applicationContext))
    }

    fun onShowDialogFragment(view: View) {
        val fragment = MyDialogFragment()
        fragment.show(supportFragmentManager, "dialog")
    }

    fun onLogin(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun hideSystemUI(view: View) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                 Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI(view: View) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun showBottomSheet(view: View) {
        ItemListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog");


//        val transaction = supportFragmentManager.beginTransaction()
//
//        // Replace whatever is in the fragment_container view with this fragment,
//        // and add the transaction to the back stack so the user can navigate back
//        transaction.replace(R.id.fragment_container, ItemFragment.newInstance(10))
//        transaction.addToBackStack(null)
//
//        // Commit the transaction
//        transaction.commit()
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var notificationId = 1

    fun urgent(view: View) {
        val channelId = "urgent"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager().createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH))
        }

        NotificationManagerCompat.from(applicationContext).notify(
                notificationId++,
                NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentTitle(channelId)
                        .setContentText(channelId)
                        .setGroup(channelId)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(Uri.parse("android.resource://$packageName/${R.raw.audio_mode_alarm_clock}"))
                        .build())

        notifySummary(Int.MAX_VALUE, channelId)
    }

    fun high(view: View) {
        val channelId = "high"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager().createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT))

        }
        NotificationManagerCompat.from(applicationContext).notify(
                notificationId++,
                NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentTitle(channelId)
                        .setContentText(channelId)
                        .setGroup(channelId)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_ALARM))
                        .build())

        notifySummary(Int.MAX_VALUE - 1, channelId)
    }

    fun medium(view: View) {
        val channelId = "medium"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager().createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW))

        }
        NotificationManagerCompat.from(applicationContext).notify(
                2,
                NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentTitle(channelId)
                        .setContentText(channelId)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build())
    }

    fun low(view: View) {
        val channelId = "low"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager().createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_MIN))
        }
        NotificationManagerCompat.from(applicationContext).notify(
                1,
                NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentTitle(channelId)
                        .setContentText(channelId)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build())
    }

    private fun notifySummary(id: Int, channelId: String) {
        NotificationManagerCompat.from(applicationContext).notify(
                channelId.hashCode(),
                NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setGroup(channelId)
                        .setGroupSummary(true)
                        .build())

    }
}

