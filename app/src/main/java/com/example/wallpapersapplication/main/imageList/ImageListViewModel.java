package com.example.wallpapersapplication.main.imageList;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.wallpapersapplication.base.AppDelegate;
import com.example.wallpapersapplication.main.Image;
import com.example.wallpapersapplication.network.Resource;

import java.util.ArrayList;
import java.util.List;

public class ImageListViewModel extends AndroidViewModel {

    private MutableLiveData<String> categoryTitleLiveData;

    public ImageListViewModel(@NonNull Application application) {
        super(application);
        categoryTitleLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Image>> observeImages() {
        return Transformations.switchMap(categoryTitleLiveData, categoryTitle ->
                ((AppDelegate) getApplication()).getRepository().getImages(categoryTitle));
    }

    public void setCategoryTitle(String categoryTitle) {
        categoryTitleLiveData.setValue(categoryTitle);
    }

    public void updateImage(Image image) {
        ((AppDelegate) getApplication()).getRepository().updateImage(image);
    }

    public void saveImage(Context context, Image image, Bitmap bitmap) {
        ((AppDelegate) getApplication()).getRepository().saveImage(context, image, bitmap);
    }
}
