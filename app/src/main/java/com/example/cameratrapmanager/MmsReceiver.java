package com.example.cameratrapmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.cameratrapmanager.MainActivity.updateImgToAllCameres;

public class MmsReceiver extends BroadcastReceiver {
    final String TAG = "MyLog";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null ) {
            updateImgToAllCameres(context);
        }
    }
}
