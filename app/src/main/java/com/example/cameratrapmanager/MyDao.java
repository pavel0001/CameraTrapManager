package com.example.cameratrapmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDao {
    @Query("SELECT * FROM traplist")
    List<TrapList> getAll();

    @Insert
    void insertAll(TrapList... text);

    @Delete
    void delete(TrapList text);
    @Update
    void updateTrap(TrapList text);
}
