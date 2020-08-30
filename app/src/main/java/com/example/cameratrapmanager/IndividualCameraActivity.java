package com.example.cameratrapmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class IndividualCameraActivity extends AppCompatActivity {
    int position;
    TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_camera);
        position = getIntent().getIntExtra("position",0);
        Toast.makeText(this, String.valueOf(position), Toast.LENGTH_SHORT).show();
        txtView = (TextView) findViewById(R.id.txtView);
        txtView.setText(MainActivity.trapList.get(position).getPos().toString());

    }
}