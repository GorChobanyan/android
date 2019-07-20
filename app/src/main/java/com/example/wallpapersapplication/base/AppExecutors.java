package com.example.wallpapersapplication.base;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private final Executor diskIO;

    private AppExecutors(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }
}
