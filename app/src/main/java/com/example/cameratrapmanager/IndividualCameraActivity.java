package com.example.cameratrapmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class IndividualCameraActivity extends AppCompatActivity implements View.OnClickListener {
    private String command;
    int position;
    boolean flagSmsStatus = true;
    ViewPager2 viewPager;  //ViewPager
    TabLayout tabLayout;
    String cameraNumber;
    TextView txtViewLabel;
    Button btnEnDisSms,btnSetPir;
    ImageButton imgBtnAddDeleteNumber, imgBtnSetTime;
    EditText edtPhone, edtLog;
    RadioGroup rGrPir, rGrNumber;
    TrapList ourTrap;
    private FragmentStateAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_camera);
        position = getIntent().getIntExtra("position", 0);
        ourTrap = MainActivity.trapList.get(position);
        txtViewLabel = (TextView) findViewById(R.id.txtViewLabel);
        cameraNumber = MainActivity.trapList.get(position).getNumber();
        txtViewLabel.setText(cameraNumber);// Set text to number camera
        viewPager = (ViewPager2) findViewById(R.id.viewPager); //ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(String.valueOf(position+1));
            }
        });
        mediator.attach();


        btnEnDisSms = (Button) findViewById(R.id.btnEnDisSms);// Enabled or disabled sms on camera
        btnSetPir = (Button) findViewById(R.id.btnSetPir);// Change status pir of PIR sensor

        imgBtnAddDeleteNumber = (ImageButton) findViewById(R.id.imgBtnAddDeleteNumber);
        imgBtnSetTime = (ImageButton) findViewById(R.id.imgBtnSetTime);

        //imgLastPhoto = (ImageView) findViewById(R.id.imgLastPhoto);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        rGrPir = (RadioGroup) findViewById(R.id.rGrPir);
        rGrNumber = (RadioGroup) findViewById(R.id.rGrNumber);
        edtLog = (EditText) findViewById(R.id.edtLog);

        imgBtnAddDeleteNumber.setOnClickListener(this);
        imgBtnSetTime.setOnClickListener(this);
        btnEnDisSms.setOnClickListener(this);
        btnSetPir.setOnClickListener(this);


        notifyLog();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadImg();
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

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            Log.i("MyTag", String.valueOf(position));
            Log.i("MyTag", ourTrap.getListUri().get(position).toString());
            return new FragmentImage().newInstance(position, ourTrap.getListUri().get(position));
        }

        @Override
        public int getItemCount() {
            return ourTrap.getListUri().size();
        }
    }


}