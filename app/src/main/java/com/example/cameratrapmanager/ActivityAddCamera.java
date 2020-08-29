package com.example.cameratrapmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ActivityAddCamera extends AppCompatActivity implements View.OnClickListener {
    EditText edtNumber, edtDegFirst, edtMinFirst, edtSecFirst, edtDegSecnd, edtMinSecnd, edtSecSecnd;
    ImageButton btnMap;
    Button btnAdd, btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);
        edtNumber = (EditText) findViewById(R.id.edtNumber);
        edtDegFirst = (EditText) findViewById(R.id.edtDegFirst);
        edtMinFirst = (EditText) findViewById(R.id.edtMinFirst);
        edtSecFirst = (EditText) findViewById(R.id.edtSecFirst);
        edtDegSecnd = (EditText) findViewById(R.id.edtDegSecnd);
        edtMinSecnd = (EditText) findViewById(R.id.edtMinSecnd);
        edtSecSecnd = (EditText) findViewById(R.id.edtSecSecnd);
        btnMap = (ImageButton) findViewById(R.id.btnMap);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnAdd:
                if(!edtNumber.getText().toString().equals("") && edtNumber.getText().length()==13
                        && edtNumber.getText().toString().toCharArray()[0]=='+'){
                    if(! MainActivity.haveTrapListNumber(edtNumber.getText().toString())) {
                        Intent intent = new Intent();

                        intent.putExtra("number", edtNumber.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else {
                        Toast.makeText(view.getContext(),"This number already added to app", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btnCancel:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.btnMap:
                Intent intentMap = new Intent(this, MapActivityGetPos.class);
                startActivity(intentMap);
                break;
            default:
                break;
        }
    }
}