package com.example.wallpapersapplication.fullScreenImage;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wallpapersapplication.base.AppDelegate;
import com.example.wallpapersapplication.main.Image;

class FullScreenImageViewModel extends AndroidViewModel {

    public FullScreenImageViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Image> getSingleImage(int imageId) {
        return ((AppDelegate) getApplication()).getRepository().getSingleImage(imageId);
    }

    public void updateImage(Image image) {
        ((AppDelegate) getApplication()).getRepository().updateImage(image);
    }

    public void saveImage(Context context, Image image, Bitmap bitmap) {
        ((AppDelegate) getApplication()).getRepository().saveImage(context, image, bitmap);
    }
}
