package com.example.wallpapersapplication.main.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.wallpapersapplication.base.AppDelegate;
import com.example.wallpapersapplication.main.Image;
import com.example.wallpapersapplication.network.Resource;

import java.util.ArrayList;

public class CategoriesViewModel extends AndroidViewModel {

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Resource<ArrayList<Image>>> getImageUrls(ArrayList<String> categoryTitles) {
        return ((AppDelegate) getApplication()).getRepository().getImageUrls(categoryTitles);
    }

    public void updateImages(ArrayList<Image> images) {
        ((AppDelegate) getApplication()).getRepository().updateImages(images);
    }
}
