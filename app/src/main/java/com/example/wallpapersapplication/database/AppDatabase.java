package com.example.wallpapersapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.wallpapersapplication.main.Image;

@Database(entities = {Image.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static String DATABASE_NAME = "wallpaper_db";

    public abstract DatabaseDao databaseDao();

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {

        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = create(context);
                }
            }
        }
        return instance;
    }

    private static AppDatabase create(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                DATABASE_NAME).build();
    }
}
