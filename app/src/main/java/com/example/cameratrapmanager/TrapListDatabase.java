package com.example.cameratrapmanager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TrapList.class}, version = 1)
public abstract class TrapListDatabase extends RoomDatabase {
    public abstract MyDao myDao();

    private static volatile TrapListDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TrapListDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrapListDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrapListDatabase.class, "trap_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


