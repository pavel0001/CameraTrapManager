package com.example.cameratrapmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ShowMapWithTrapActivity extends AppCompatActivity implements OnMapReadyCallback {
    Spinner spinner;
    SupportMapFragment mapFragment;
    GoogleMap map;
    Marker mark;
    ImageButton btnBack;
    int selectTrap;
    ArrayList<LatLng> markList;
    ArrayList<TrapList> mapTrapList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map_with_trap);
        mapTrapList = MainActivity.trapList;
        selectTrap = getIndexWithNumber(getIntent().getStringExtra("number"));
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        String[] data = new String[mapTrapList.size()];
        for(int i = 0; i<mapTrapList.size();i++){
            data[i] = mapTrapList.get(i).getNumber();
        }

        markList = new ArrayList<>();
        for(TrapList x: mapTrapList)
        markList.add(x.getPos());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.idSpinner);
        spinner.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        spinner.setSelection(selectTrap);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mapTrapList.get(position).getPos())
                        .zoom(12)
                        .bearing(45)
                        .tilt(20)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        View.OnClickListener onClck = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
        btnBack.setOnClickListener(onClck);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;

       MarkerOptions[] markers = new MarkerOptions[markList.size()];
        for (int i = 0; i < markList.size(); i++) {
/*            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mapTrapList.get(i).getUri()));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            markers[i] = new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .position(markList.get(i))
                    .icon(BitmapDescriptorFactory.defaultMarker());
            googleMap.addMarker(markers[i]);
        }


    }
    public int getIndexWithNumber(String number){
        int ind=0;
        for(int i = 0;i<mapTrapList.size();i++){
            if(mapTrapList.get(i).getNumber().equals(number))
                ind = i;
        }
        return ind;
    }
}
