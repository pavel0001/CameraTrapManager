package com.example.cameratrapmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.cameratrapmanager.MmsLoader.LoadLastMmsImage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    public static ArrayList<TrapList> trapList;
    public static LatLng pos;
    public static MyRecyclerViewAdapter adapter;
    View.OnClickListener onClck, onClckDelete;
    private static final int MY_PERMISSIONS_REQUEST_CODE =0 ;
    public static final  String TAG = "MyLog";
    public static int FIRST_SWOU_ADD_DIALOG = 0;
    FloatingActionButton btnAdd;
    public static SmsManager smsManager;
    MyDao myDao;
    TrapListDatabase db;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = TrapListDatabase.getDatabase(this);
        myDao = db.myDao();
        trapList = new ArrayList<TrapList>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        smsManager = SmsManager.getDefault();
        Log.d(TAG,"onCreate MainActivity" );
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permissionStatusReceive = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED || permissionStatusReceive == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.RECEIVE_MMS,
                    Manifest.permission.RECEIVE_WAP_PUSH,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSIONS_REQUEST_CODE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart mainActivity" );
        updateImgToAllCameres(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume MainActivity" );
        if( ! trapList.isEmpty()) {
            for(TrapList x: trapList)
                myDao.updateTrap(x);
        }
        try {
            if (myDao != null) {
                ArrayList<TrapList> tmp = (ArrayList<TrapList>) myDao.getAll();
                trapList.clear();
                for( TrapList x: tmp){
                    trapList.add(new TrapList(x));
                }
            }
        }
        catch (Exception e){
            Log.d(TAG, "Error load DB");
        }
        adapter.notifyDataSetChanged();
      if(trapList.isEmpty() && FIRST_SWOU_ADD_DIALOG == 0) {
            addCameraToList();
          FIRST_SWOU_ADD_DIALOG = 1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                    trapList.add(new TrapList(number));
                }
                if( ! trapList.isEmpty()) {
                    myDao.insertAll(trapList.get(trapList.size() - 1));
                    adapter.notifyDataSetChanged();
                }
            }
            else {
                Toast.makeText(this, "Cancel",Toast.LENGTH_SHORT).show();
            }

        }
        else {
        }
    }
    public static void updateImgToAllCameres(Context context){
        final LoadLastMmsImage loader = new LoadLastMmsImage(context);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < trapList.size(); i++) {
                        Uri uri = loader.writeAllMms(trapList.get(i).getNumber());
                        trapList.get(i).setUri(uri.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
            thread.start();
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
            if(x.getNumber().equals(number))
                return true;
        }
        return false;
    }
    private void deleteCamera(int position){
        if(position < trapList.size() && trapList.size() > 0) {
            if(trapList.get(position) != null) {
                myDao.delete(trapList.get(position));
                trapList.remove(position);
                adapter.notifyDataSetChanged();
            }
        }
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
                        refreshCamera(trapList.get(myViewHolder.getAdapterPosition()).getNumber());
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
        holder.txtLabel.setText(elementsList.getNumber());
        holder.txtBat.setText(elementsList.getBattery());
        holder.txtSignal.setText(elementsList.getSignal());
        holder.txtStor.setText(elementsList.getStorage());
        holder.imgIcon.setImageResource(R.mipmap.ic_launcher_cameratrap_foreground);
        //myDao.updateTrap(elementsList);


        }

        @Override
        public int getItemCount() {
            return trapList.size();
        }
    }
}
