package com.example.wallpapersapplication.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.wallpapersapplication.base.AppDelegate;
import com.example.wallpapersapplication.network.Resource;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Resource<ArrayList<Image>>> getImageUrls(ArrayList<String> categoryTitles) {
        return ((AppDelegate) getApplication()).getRepository().getImageUrls(categoryTitles);
    }

    public void insertImages(ArrayList<Image> images) {
        ((AppDelegate) getApplication()).getRepository().insertImages(images);
    }

    public LiveData<List<Image>> getAllImages() {
        return ((AppDelegate) getApplication()).getRepository().getAllImages();
    }
}
