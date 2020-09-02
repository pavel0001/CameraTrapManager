package com.example.cameratrapmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    public static ArrayList<TrapList> trapList;
    public static LatLng pos;
    public static MyRecyclerViewAdapter adapter;
    View.OnClickListener onClck, onClckDelete;
    private static final int MY_PERMISSIONS_REQUEST_CODE =0 ;
    FloatingActionButton btnAdd;
    public static SmsManager smsManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trapList = new ArrayList<TrapList>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        smsManager = SmsManager.getDefault();
        /*trapList.add(new TrapList("375251112233","53 27 32 - 29 32 11"));
        trapList.add(new TrapList("375251112234","53 01 54 - 28 32 11"));*/
       // trapList.add(new TrapList("375255247608",new LatLng(54,28)));
        Log.d("My_Log","onCreate MainActivity" );
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permissionStatusReceive = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED || permissionStatusReceive == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSIONS_REQUEST_CODE);
        }
        if(trapList.isEmpty()) {
            addCameraToList();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnAdd:
                addCameraToList();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String number = data.getStringExtra("number");
                /*Double lat = Double.parseDouble(data.getStringExtra("lat"));
                Double longi = Double.parseDouble(data.getStringExtra("longi"));*/
                if(pos != null) {
                    trapList.add(new TrapList(number, pos));
                }
                else {
                    trapList.add(new TrapList(number, new LatLng(0, 0)));
                }

                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(this, "Cancel",Toast.LENGTH_SHORT).show();
            }

        }
        else {
        }
    }
    public void refreshCamera(String number){
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        smsManager.sendTextMessage(number, null, "*160#", null, null);

    }
    public static void sendCommandToCamera(String number, String command){
        smsManager.sendTextMessage(number, null, command, null, null);

    }
    public void addCameraToList(){
        Intent intent = new Intent(this, ActivityAddCamera.class);
        startActivityForResult(intent, 1);
    }
    public static boolean haveTrapListNumber(String number){
        for(TrapList x: trapList){
            if(x.number.equals(number))
                return true;
        }
        return false;
    }
    private void deleteCamera(int position){
        trapList.remove(position);
        adapter.notifyDataSetChanged();
    }


    class MyViewHolder extends ViewHolder{
        LinearLayout allLayout;
        TextView txtLabel, txtBat, txtStor, txtSignal, txtPos;
        ImageButton btnRef, btnDelete;
        ImageView imgIcon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            allLayout = (LinearLayout) findViewById(R.id.imgBtnDelete);
            txtLabel = (TextView) itemView.findViewById(R.id.txtLabel);
            txtBat = (TextView) itemView.findViewById(R.id.txtBat);
            txtStor = (TextView) itemView.findViewById(R.id.txtStor);
            txtSignal = (TextView) itemView.findViewById(R.id.txtSignal);
            txtPos = (TextView) itemView.findViewById(R.id.txtPos);
            btnRef = (ImageButton) itemView.findViewById(R.id.btnRef);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
        }
    }
    class MyRecyclerViewAdapter extends Adapter<MyViewHolder>{
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                Intent intent = new Intent(getApplicationContext(), IndividualCameraActivity.class);
                intent.putExtra("position",itemPosition );
                startActivity(intent);
            }
        };
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
            view.setOnClickListener(mOnClickListener);
            final MyViewHolder myViewHolder = new MyViewHolder(view);
            onClck = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String tmp = String.valueOf(myViewHolder.getAdapterPosition());
                    if (myViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        refreshCamera(trapList.get(myViewHolder.getAdapterPosition()).number);
                    }
                }
            };
            onClckDelete = new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    deleteCamera(myViewHolder.getAdapterPosition());
                }
            };
            myViewHolder.btnDelete.setOnClickListener(onClckDelete);
            myViewHolder.btnRef.setOnClickListener(onClck);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TrapList elementsList = trapList.get(holder.getAdapterPosition());
        holder.txtLabel.setText(elementsList.number);
        holder.txtBat.setText(elementsList.battery);
        holder.txtSignal.setText(elementsList.signal);
        holder.txtStor.setText(elementsList.storage);
        holder.imgIcon.setImageResource(R.mipmap.ic_launcher_cameratrap_foreground);

        }

        @Override
        public int getItemCount() {
            return trapList.size();
        }
    }
}
