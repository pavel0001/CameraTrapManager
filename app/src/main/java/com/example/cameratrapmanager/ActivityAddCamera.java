package com.example.cameratrapmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class ActivityAddCamera extends AppCompatActivity implements View.OnClickListener {
    EditText edtNumber, edtDegFirst, edtMinFirst, edtSecFirst, edtDegSecnd, edtMinSecnd, edtSecSecnd;
    ImageButton btnMap, imgBtnContacts;
    Button btnAdd, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);
        edtNumber = (EditText) findViewById(R.id.edtNumber);
        edtDegFirst = (EditText) findViewById(R.id.edtDegFirst);
        edtDegSecnd = (EditText) findViewById(R.id.edtDegSecnd);
        btnMap = (ImageButton) findViewById(R.id.btnMap);
        imgBtnContacts = (ImageButton) findViewById(R.id.imgBtnContacts);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        imgBtnContacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                if (!edtNumber.getText().toString().equals("") && edtNumber.getText().length() <= 20
                        && edtNumber.getText().toString().toCharArray()[0] == '+') {
                    if (!MainActivity.haveTrapListNumber(edtNumber.getText().toString())) {
                        try {
                            Intent intent = new Intent();
                            intent.putExtra("number", edtNumber.getText().toString());
                            if (!edtDegFirst.getText().equals("") && !edtDegSecnd.getText().equals("")) {
                                MainActivity.pos = new LatLng(Double.parseDouble(edtDegFirst.getText().toString()),
                                        Double.parseDouble(edtDegSecnd.getText().toString()));
                            }
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (NullPointerException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), "This number already added to app", Toast.LENGTH_SHORT).show();
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
                startActivityForResult(intentMap, 1);
                break;
            case R.id.imgBtnContacts:
                Intent intentPickCont = new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                intentPickCont.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intentPickCont, 2);

                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    edtDegFirst.setText(String.valueOf(MainActivity.pos.latitude));
                    edtDegSecnd.setText(String.valueOf(MainActivity.pos.longitude));
                } else {
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                            Cursor cursor = null;
                            try {
                                String phoneNo = null ;
                                String name = null;
                                Uri uri = data.getData();
                                cursor = getContentResolver().query(uri, null, null, null, null);
                                cursor.moveToFirst();
                                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                phoneNo = cursor.getString(phoneIndex);
                                edtNumber.setText(phoneNo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                }  else {
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}