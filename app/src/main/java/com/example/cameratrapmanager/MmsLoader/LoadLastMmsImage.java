package com.example.cameratrapmanager.MmsLoader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Telephony;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import static android.provider.Telephony.BaseMmsColumns.DATE;
import static android.provider.Telephony.BaseMmsColumns.MESSAGE_BOX;
import static android.provider.Telephony.BaseMmsColumns.MESSAGE_BOX_INBOX;
import static android.provider.Telephony.BaseMmsColumns._ID;
import static android.provider.Telephony.MmsSms.TYPE_DISCRIMINATOR_COLUMN;

public class LoadLastMmsImage extends ContentResolver {

    private static final String TAG = "MyTag" ;
    public static ContentResolver resolver;
    public LoadLastMmsImage(@Nullable Context context) {
        super(context);
        resolver = context.getContentResolver();
    }

    public static ArrayList<Uri> writeAllMms(String number) {
       // Log.i(TAG, "writeAllMms");
        ArrayList<Uri> listUri = new ArrayList<Uri>();
        final String TRAP_NUMBER = number;
        Bitmap tmp = null;
        Uri uriQ = null;
        int min_id=0;
        Date min_date = new Date(100);
        //ContentResolver contentResolver = ContentResolver.getContentResolver();
        final String[] projection = new String[]{_ID, TYPE_DISCRIMINATOR_COLUMN, MESSAGE_BOX, DATE};
        Uri uri = Uri.parse("content://mms-sms/complete-conversations/");
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            do {

                String desctipt = cursor.getString(cursor.getColumnIndex(TYPE_DISCRIMINATOR_COLUMN));
                String box = cursor.getString(cursor.getColumnIndex(MESSAGE_BOX));
                String id = cursor.getString(cursor.getColumnIndex(_ID));

                if (desctipt.equals("mms") && box.equals(String.valueOf(MESSAGE_BOX_INBOX))) {
                    Uri uriMmsLevel = Uri.parse("content://mms/" + id + "/addr");
                    String selectionMmsLevel = "msg_id = " + id;
                    Cursor cursorMmsLevel = resolver.query(uriMmsLevel, null, selectionMmsLevel, null, null);


                    if (cursorMmsLevel.moveToFirst()) {
                        do {
                            String adr = cursorMmsLevel.getString(cursorMmsLevel.getColumnIndex("address"));
                            Integer type = Integer.parseInt(cursorMmsLevel.getString(cursorMmsLevel.getColumnIndex("type")));
                            if( adr.equals(TRAP_NUMBER) && type == 137) {
                                Date date = new Date((long) cursor.getLong(cursor.getColumnIndex(Telephony.BaseMmsColumns.DATE))*1000);
                                if(date.after(min_date)){
                                    min_date = date;
                                    min_id = Integer.parseInt(id);
                                    listUri = loadOnList(listUri,getImgMms(min_id));
                                    //SimpleDateFormat sForm = new SimpleDateFormat("yyyy.MMM.dd hh:mm");
                                }

                            }
                        } while (cursorMmsLevel.moveToNext());
                        cursorMmsLevel.close();
                    }
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
        return listUri;
    }
    public static Uri getImgMms(int id){
        Bitmap tmp = null;
        String uriR = null;
        String selectionPart = "mid=" + id;
        Uri uri = Uri.parse("content://mms/part");
        Cursor cPart = resolver.query(uri, null,
                selectionPart, null, null);
        if (cPart.moveToFirst()) {
            do {
                String partId = cPart.getString(cPart.getColumnIndex("_id"));
                String type = cPart.getString(cPart.getColumnIndex("ct"));
                if ("image/jpeg".equals(type) || "image/bmp".equals(type) ||
                        "image/gif".equals(type) || "image/jpg".equals(type) ||
                        "image/png".equals(type)) {
                    uriR = partId;
                }
            } while (cPart.moveToNext());
        }
        return Uri.parse("content://mms/part/" + uriR);
    }
    public static ArrayList<Uri> loadOnList(ArrayList<Uri> list, Uri uri){
        if(list.size()< 10){
            list.add(0, uri);
        }
        else {
            list.remove(9);
            list.add(0, uri);
        }
        return list;
    }
}
