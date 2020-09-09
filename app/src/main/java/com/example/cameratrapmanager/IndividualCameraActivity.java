package com.example.cameratrapmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cameratrapmanager.MmsLoader.LoadLastMmsImage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IndividualCameraActivity extends AppCompatActivity implements View.OnClickListener {
    private String command;
    int position;
    boolean flagSmsStatus = true;
    String cameraNumber;
    TextView txtViewLabel;
    Button btnTakePhoto, btnDelete, btnEnDisSms,btnSetPir;
    ImageButton imgBtnAddDeleteNumber, imgBtnSetTime;
    ImageView imgLastPhoto;
    EditText edtPhone, edtLog;
    RadioGroup rGrPir, rGrNumber;
    TrapList ourTrap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_camera);
        //final TrapList ourTrap = MainActivity.trapList.get(position);
        position = getIntent().getIntExtra("position", 0);
        ourTrap = MainActivity.trapList.get(position);
        txtViewLabel = (TextView) findViewById(R.id.txtViewLabel);
        cameraNumber = MainActivity.trapList.get(position).getNumber();
        txtViewLabel.setText(cameraNumber);// Set text to number camera

        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);//Send message *500# to camera and take new photo
        btnDelete = (Button) findViewById(R.id.btnDelete);// Delete this camera
        btnEnDisSms = (Button) findViewById(R.id.btnEnDisSms);// Enabled or disabled sms on camera
        btnSetPir = (Button) findViewById(R.id.btnSetPir);// Change status pir of PIR sensor

        imgBtnAddDeleteNumber = (ImageButton) findViewById(R.id.imgBtnAddDeleteNumber);
        imgBtnSetTime = (ImageButton) findViewById(R.id.imgBtnSetTime);

        imgLastPhoto = (ImageView) findViewById(R.id.imgLastPhoto);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        rGrPir = (RadioGroup) findViewById(R.id.rGrPir);
        rGrNumber = (RadioGroup) findViewById(R.id.rGrNumber);
        edtLog = (EditText) findViewById(R.id.edtLog);

        imgBtnAddDeleteNumber.setOnClickListener(this);
        imgBtnSetTime.setOnClickListener(this);
        btnEnDisSms.setOnClickListener(this);
        btnSetPir.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);



        notifyLog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final LoadLastMmsImage loader = new LoadLastMmsImage(getApplicationContext());
        if (ourTrap.getUri() == null) {
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    Uri imgUri = loader.writeAllMms(cameraNumber);
                    loadImg(imgUri);

                }
            };
            handler.post(run);
        } else {
            loadImg(Uri.parse(ourTrap.getUri()));
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgBtnAddDeleteNumber:
                if (!edtPhone.getText().toString().equals("") && edtPhone.getText().toString().length() == 12) {
                    switch(rGrNumber.getCheckedRadioButtonId()){
                        case R.id.radBtnAdd:
                            command = "*100#"+edtPhone.getText().toString()+"#";
                            MainActivity.sendCommandToCamera(cameraNumber, command);
                            savedInLog("Add : "+edtPhone.getText().toString());
                            Toast.makeText(getApplicationContext(),"Add", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.radBtnDel:
                            command = "*101#"+edtPhone.getText().toString()+"#";
                            MainActivity.sendCommandToCamera(cameraNumber, command);
                            savedInLog("Delete : "+edtPhone.getText().toString());
                            Toast.makeText(getApplicationContext(),"Del", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"incorrect number", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgBtnSetTime:
                DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
                String date = df.format(Calendar.getInstance().getTime());
                command = "*205#"+date+"#";
                MainActivity.sendCommandToCamera(cameraNumber, command);
                savedInLog("Set date.");
                break;
            case R.id.btnEnDisSms:
                command ="*140#";
                if(flagSmsStatus){
                    command +="2#";
                    flagSmsStatus = false;
                    savedInLog("Sms disabled.");
                }
                else {
                    command +="0#";
                    flagSmsStatus = true;
                    savedInLog("Sms enabled.");
                }
                MainActivity.sendCommandToCamera(cameraNumber, command);

                break;
            case R.id.btnSetPir:
                command = "*202#";
                if(rGrPir.getCheckedRadioButtonId() != -1) {
                    switch (rGrPir.getCheckedRadioButtonId()) {
                        case R.id.pirH:
                            command += "0#";
                            savedInLog("PIR high");

                            break;
                        case R.id.pirM:
                            command += "1#";
                            savedInLog("PIR medium");
                            break;
                        case R.id.pirL:
                            command += "2#";
                            savedInLog("PIR low");
                            break;
                        case R.id.pirO:
                            savedInLog("PIR off");
                            command += "3#";
                            break;
                        default:
                            break;

                    }
                    MainActivity.sendCommandToCamera(cameraNumber, command);
                }
                break;
            case R.id.btnTakePhoto:
                command = "*500#";
                MainActivity.sendCommandToCamera(cameraNumber, command);
                savedInLog("Take photo");
                break;
            case R.id.btnDelete:
                edtLog.setText("");
                MainActivity.trapList.get(position).clearLogCommand();
                break;
            default:
                break;
        }
        notifyLog();
    }
    public void savedInLog(String command){
        DateFormat df = new SimpleDateFormat("[dd.MM.YY hh:mm]: ");
        String date = df.format(Calendar.getInstance().getTime());
        MainActivity.trapList.get(position).addLogCommand(date+command+"\n");
        Log.d("MyLog","addLogCommand: "+date+command+"\n" );
    }
    public void notifyLog(){
        ArrayList<String> tmp = MainActivity.trapList.get(position).getLogCommand();
        Log.d("MyLog","notifyLog: "+tmp.toString() );
        String tmpSec = "";
        edtLog.setText("");
        for(String x: tmp){
            tmpSec += x;
        }
        edtLog.setText(tmpSec);
    }
    public void loadImg(Uri img){
        Glide
                .with(this)
                .load(img)
                .placeholder(R.mipmap.ic_launcher_cameratrap_foreground)
                .into(imgLastPhoto);
        ourTrap.setUri(img.toString());
    }
}