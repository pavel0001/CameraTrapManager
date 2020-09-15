package com.example.cameratrapmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivityGetPos extends AppCompatActivity implements OnMapReadyCallback  {
    SupportMapFragment mapFragment;
    GoogleMap map;
    EditText edtLat, edtLong;
    Button btnAdd;
    Marker cursor;
    boolean flag=false;
    final String TAG = "myLog";
    private List<LatLng> places = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_get_pos);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        //places.add(new LatLng(54.754724, 28.621380));

        edtLat = (EditText) findViewById(R.id.edtLat);
        edtLong = (EditText) findViewById(R.id.edtLong);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        View.OnClickListener onClck = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cursor != null && flag) {
                    Intent intent = new Intent();
                    Log.d("MyTag", cursor.getPosition().toString());
                    MainActivity.pos = cursor.getPosition();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Click on the map", Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnAdd.setOnClickListener(onClck);




    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
/*        MarkerOptions[] markers = new MarkerOptions[places.size()];
        for (int i = 0; i < places.size(); i++) {
            markers[i] = new MarkerOptions()
                    .position(places.get(i));
            googleMap.addMarker(markers[i]);
        }*/

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);
                if(flag) {
                    cursor.setPosition(latLng);
                }
                else{
                    cursor = googleMap.addMarker(new MarkerOptions()
                    .position(latLng));
                    flag = true;
                }
                    edtLat.setText(String.valueOf(latLng.latitude));
                    edtLong.setText(String.valueOf(latLng.longitude));

            }
        });

    }
}