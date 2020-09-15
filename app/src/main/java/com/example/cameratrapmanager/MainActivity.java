package com.example.cameratrapmanager;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.example.cameratrapmanager.MmsLoader.LoadLastMmsImage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String NOTIFICATION_CHANNEL_ID = "MainChannel";
    //private static final int NOTIFICATION_CHANNEL_ID = 214;
    private static final CharSequence NOTIFICATION_CHANEL_NAME = "CameraNot";
    private static final String NOTIFICATION_CHANNEL_DESC = "Notification about new mms";
    public static boolean FLAG_STATUS_LOAD = false;
    RecyclerView recyclerView;
    public static ArrayList<TrapList> trapList;
    public static LatLng pos;
    public static MyRecyclerViewAdapter adapter;
    View.OnClickListener onClck, onClckDelete, onClckMap;
    private static final int MY_PERMISSIONS_REQUEST_CODE =0 ;
    public static final  String TAG = "MyTag";
    public static int FIRST_SWOU_ADD_DIALOG = 0;
    FloatingActionButton btnAdd;
    public static SmsManager smsManager;
    MyDao myDao;
    static Handler handler;
    TrapListDatabase db;
    public int flag = 0;
    final int NOTIFICATION_STORAGE = 356;
    NotificationManager notifManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notifManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
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


/*        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {
                    public boolean onMove(RecyclerView recyclerView,
                                          ViewHolder viewHolder, ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        // move item in `fromPos` to `toPos` in adapter.
                        return true;// true if moved, false otherwise
                    }
                    public void onSwiped(ViewHolder viewHolder, int direction) {
                        // remove from adapter
                    }
                });*/
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
            if (myDao != null && flag == 0) {
                ArrayList<TrapList> tmp = (ArrayList<TrapList>) myDao.getAll();
                trapList.clear();
                for( TrapList x: tmp){
                    trapList.add(new TrapList(x));
                }
                Log.i(TAG, "updateImgToAllCameres  onResume");
                updateImgToAllCameres(this);
                flag = 1;
            }
        }
        catch (Exception e){
            Log.i(TAG, "Error load DB");
        }
        adapter.notifyDataSetChanged();
      if(trapList.isEmpty() && FIRST_SWOU_ADD_DIALOG == 0) {
            addCameraToList();
          FIRST_SWOU_ADD_DIALOG = 1;
        }
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
                if(pos != null) {
                    trapList.add(new TrapList(number, pos));
                }
                else {
                    trapList.add(new TrapList(number));
                }
                if( ! trapList.isEmpty()) {
                    trapList.get(trapList.size()-1).setFLAG_LOAD(true);
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
                    for(TrapList x: trapList) {
                    x.setFLAG_LOAD(true);
                }
                    for (int i = 0; i < trapList.size(); i++) {
                        ArrayList<Uri> tmp = loader.writeAllMms(trapList.get(i).getNumber());
                        trapList.get(i).setListUri(tmp);
                        trapList.get(i).setFLAG_LOAD(false);
                        Runnable rnbl = new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        };
                        handler = new Handler(Looper.getMainLooper());
                        handler.post(rnbl);
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
       // showNot("Camera "+number+" is refreshing");

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
        ImageView imgIcon, imgGps;
        ProgressBar prgrssBar;
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
            imgGps = (ImageView) itemView.findViewById(R.id.imgGps);
            prgrssBar = (ProgressBar) itemView.findViewById(R.id.prgrssBar);
        }
    }
    class MyRecyclerViewAdapter extends Adapter<MyViewHolder>{
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                if(trapList.get(itemPosition).isFLAG_LOAD()){
                    Toast.makeText(getApplicationContext(),"Loading, try later", Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(getApplicationContext(), IndividualCameraActivity.class);
                    intent.putExtra("position", itemPosition);
                    startActivity(intent);
                }
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
            onClckMap = new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int pos = myViewHolder.getAdapterPosition();
                    Intent intent = new Intent(getApplicationContext(), ShowMapWithTrapActivity.class);
                    intent.putExtra("number", trapList.get(pos).getNumber());
                    startActivity(intent);

                }
            };
            myViewHolder.btnDelete.setOnClickListener(onClckDelete);
            myViewHolder.btnRef.setOnClickListener(onClck);
            myViewHolder.imgGps.setOnClickListener(onClckMap);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TrapList elementsList = trapList.get(holder.getAdapterPosition());
            holder.txtLabel.setText(elementsList.getNumber());
            holder.txtBat.setText(elementsList.getBattery());
            holder.txtSignal.setText(elementsList.getSignal());
            holder.txtStor.setText(elementsList.getStorage());
            if (elementsList.getListUri().size() > 0) {
                holder.prgrssBar.setVisibility(View.INVISIBLE);
                Glide.with(getApplicationContext())
                        .load(elementsList.getListUri().get(0))
                        .placeholder(R.mipmap.ic_launcher_cameratrap_foreground)
                        .centerCrop()
                        .into(holder.imgIcon);
            } else {
                holder.imgIcon.setImageResource(R.color.primaryColor);
                holder.prgrssBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return trapList.size();
        }
    }
   void showNot(String text){
            final Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_STORAGE, contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            final Notification notification =
                    new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.ic_launcher_cameratrap_foreground)
                            .setContentTitle("CameraTrap Manager")
                            .setContentText(text)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .build();
            notifManager.notify(NOTIFICATION_STORAGE, notification);

    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription(NOTIFICATION_CHANNEL_DESC);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
        }
    }
}
