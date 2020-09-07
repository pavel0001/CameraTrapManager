package com.example.cameratrapmanager;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    public static App instance;

    private TrapListDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, TrapListDatabase.class, "trap_database")
                .allowMainThreadQueries()
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public TrapListDatabase getDatabase() {
        return database;
    }
}

