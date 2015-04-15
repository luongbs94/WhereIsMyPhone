package com.ln.whereismyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ln.service.MyService;

/**
 * Created by luong on 15/04/2015.
 */
public class ConnectionBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, MyService.class);
        if (isOnline(context)) {
           context.startService(service);

        }else{
            context.stopService(service);
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }
}
