package com.ln.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ln.whereismyphone.FlashAlert;
import com.ln.whereismyphone.Helper;

import java.net.URISyntaxException;
import java.util.Date;


/**
 * Created by luong on 12/04/2015.
 */

public class MyService extends Service {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://128.199.137.159:3000/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    SharedPreferences sharedPreferences;
    String mySecurityCode;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(Helper.SHAREPREFERENCE,4);
        mySecurityCode = sharedPreferences.getString(Helper.SECURITYPASSWORD, "");

        mSocket.on("messageReceived", onMessageReceive);

        mSocket.connect();
        mSocket.emit("login", mySecurityCode);
        Log.d("onCreate","onCreate");
    }

    private Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String username = args[0].toString();
            String message = args[1].toString();
            Log.d("message", message);
            String response = "";
            if(message.equals("SMS History")) {
                response = getSMS();
            }

            if(message.equals("Call History")){
                response = getCallLog();
                Log.d("call history", "call");
                Log.d("response", response);
            }

            if(message.equals("Contact")){
                response = getContacts();
            }

            if(message.equals("Flash")){
                FlashAlert flashAlert = new FlashAlert(100, 100, 4);
                (new Thread(flashAlert)).start();
            }
            mSocket.emit("sendMessage", username, response);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand","onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSocket.off("messageReceived", onMessageReceive);
        mSocket.disconnect();
        super.onDestroy();

    }

    public String getSMS(){

        StringBuffer sb = new StringBuffer();
        String[] reqCols = new String[] { "_id", "address", "body" };
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), reqCols, null, null, null);
        if(cursor != null){
            sb.append("SMS LOG:\n");
            while(cursor.moveToNext()){
                String msgData = "\n";
                for(int idx=0; idx<cursor.getColumnCount(); idx++){
                    msgData += cursor.getColumnName(idx) + ": " + cursor.getString(idx) + "\n";
                }
                sb.append(msgData);
                sb.append("------------------------------------");
            }

        }
        cursor.close();
        return sb.toString();
    }

    public String getCallLog(){

        StringBuffer sb = new StringBuffer();
        Cursor callLogCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if(callLogCursor != null){
            sb.append("CALL LOG:\n");
            while (callLogCursor.moveToNext()){
                /*Get ID of call*/
                String id = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls._ID));

                /*Get Contact Name*/
                String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                if (name == null)
                    name = "No Name";

                /*Get Contact Number*/
                String number = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));

                /*Get Date and time information*/
                long dateTimeMillis = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE));
                Date date = new Date(dateTimeMillis);
                long duration = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DURATION));

                /*Get Call Type*/
                int callType = callLogCursor.getInt(callLogCursor.getColumnIndex(CallLog.Calls.TYPE));
                String type = null;
                switch (callType){
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "OUTGOING";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        type = "INCOMING";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        type = "MISSED";
                        break;
                }
                sb.append("\nID: " + id + "\nName: " + name + "\nNumber: " + number +
                        "\nDate Time: " + date.toString() + "\nDuration: " + duration + " seconds" +
                        "\nType: " + type);
                sb.append("\n------------------------------------");
            }

        }
        callLogCursor.close();
        return sb.toString();
    }

    public String getContacts(){

        StringBuffer sb = new StringBuffer();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_ALTERNATIVE);
        sb.append("CONTACTS LIST:\n");
        if(cursor != null){
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                sb.append("\nName: " + name + "\nPhone Number: " + phone);
                sb.append("\n------------------------------------");
            }

        }
        cursor.close();
        return sb.toString();
    }

}
