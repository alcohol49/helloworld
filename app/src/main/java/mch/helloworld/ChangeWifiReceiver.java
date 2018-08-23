package mch.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ChangeWifiReceiver extends BroadcastReceiver {

    public static final String TAG = "ChangeWifiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean enable = Boolean.parseBoolean(intent.getStringExtra("wifi"));

        Log.d(TAG, "onReceive: " + enable);
        WifiManager wfm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wfm.setWifiEnabled(enable);
    }
}
