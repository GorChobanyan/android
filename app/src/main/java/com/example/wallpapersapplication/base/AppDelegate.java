package com.example.wallpapersapplication.base;

import android.app.Application;

import com.example.wallpapersapplication.base.DataRepository;
import com.example.wallpapersapplication.database.AppDatabase;
import com.example.wallpapersapplication.database.DatabaseDao;

public class AppDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        getRepository();
    }

    private DatabaseDao getDatabaseDao() {
        return AppDatabase.getInstance(this).databaseDao();
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabaseDao());
    }
}

