package com.example.cameratrapmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.*;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<TrapList> trapList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trapList = new ArrayList<TrapList>();

    }
    class MyViewHolder extends ViewHolder{
        //Todo
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
/*
Остановился на этапе добавления во вью холдер полей (их около 5)
 */