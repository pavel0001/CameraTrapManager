package com.example.cameratrapmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;



public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() != null ) {

            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            String sms_from = messages[0].getDisplayOriginatingAddress();
            if (numberIsCamera(sms_from)) {
                StringBuilder bodyText = new StringBuilder();
                for (int i = 0; i < messages.length; i++) {
                    bodyText.append(messages[i].getMessageBody());
                }
                String body = bodyText.toString();
                messageSeparate(body, sms_from);
                abortBroadcast();
            }

        }
    }
    public boolean numberIsCamera(String number){
        for(TrapList x: MainActivity.trapList){
            if(number.equalsIgnoreCase(x.number)) return true;

        }
        return false;
    }

    public void messageSeparate(String message, String number) {
        //Log.d("My_Log",message);
        ArrayList<TrapList> list = MainActivity.trapList;
        int listIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).number.equals(number))
                listIndex = i;                // index of camera trap who send answer
        }

        String[] str = message.split(",");
        for (int i = 0; i < str.length; i++) {
            if (str[i].contains("Signal")) {
                String numb = str[i].split(":")[1];
                list.get(listIndex).setSignal(numb + " of 5");
            } else if (str[i].contains("Battery")) {
                String numb = str[i].split(":")[1];
                if (isNumeric(numb)) {
                    Integer val = Integer.parseInt(numb) * 20;
                    numb = val.toString() + "%";
                } else {
                    numb += " of 5";
                }
                list.get(listIndex).setBattery(numb);
            } else if (str[i].contains("M/")) {
                //list.get(listIndex).setStorage(str[i].replaceAll("([(,)])",""));
                String tmp = str[i].replaceAll("([(,)])", "").replaceAll("M", "");
                String[] tmpArr = tmp.split("/");
                if (tmpArr.length == 2) {
                    double percent = (double) (Double.parseDouble(tmpArr[0]) / Double.parseDouble(tmpArr[1]) * 100);
                    list.get(listIndex).setStorage(String.valueOf((int) percent / 1) + "%");
                }
            }
            else if (str[i].contains("SD total")){
                double allMemory = Double.parseDouble(str[i].split(":")[1].replaceAll("M", ""));//остановился тут
                double haveMemory = (double) Double.parseDouble(str[i+1].split(":")[1].replaceAll("M",""));
                haveMemory = haveMemory/allMemory*100;
                list.get(listIndex).setStorage(String.valueOf((int) haveMemory/1)+"%");
            }
        }
        MainActivity.adapter.notifyItemChanged(listIndex);

    }
    private static boolean isNumeric(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}