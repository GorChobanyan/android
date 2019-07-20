package com.example.wallpapersapplication.main.downloadFavorite;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wallpapersapplication.base.AppDelegate;
import com.example.wallpapersapplication.main.Image;

import java.util.ArrayList;
import java.util.List;

class DownloadFavoritesViewModel extends AndroidViewModel {

    public DownloadFavoritesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Image>> getFavoriteImages() {
        return ((AppDelegate) getApplication()).getRepository().getFavoriteImages();
    }

    public LiveData<List<Image>> getAllImages() {
        return ((AppDelegate) getApplication()).getRepository().getAllImages();
    }

    public void updateImage(Image image) {
        ((AppDelegate) getApplication()).getRepository().updateImage(image);
    }

    public void removeDownloadedImage(Context context, Image image) {
        ((AppDelegate) getApplication()).getRepository().removeDownloadedImage(context, image);
    }

    public void saveImage(Context context, Image image, Bitmap bitmap) {
        ((AppDelegate) getApplication()).getRepository().saveImage(context, image, bitmap);
    }

    public ArrayList<Pair<Image, Bitmap>> getDownloadedImages(String albumName, List<Image> images) {
        return ((AppDelegate) getApplication()).getRepository().getDownloadedImages(albumName, images);
    }
}
