package com.example.cameratrapmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MmsReceiver extends BroadcastReceiver {
    final String TAG = "MyLog";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null ) {
            Toast.makeText(context,"Received MMS MmsReceiver.java",Toast.LENGTH_LONG).show();
            log("intent.getAction()= "+intent.getAction());
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            String sms_from = messages[0].getDisplayOriginatingAddress();
            log(sms_from);
            //TODO how get mobile number
        }
    }
    public void log(String text){
        Log.i(TAG, text);
    }
}
