package com.example.wallpapersapplication.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.wallpapersapplication.Utils;
import com.example.wallpapersapplication.database.DatabaseDao;
import com.example.wallpapersapplication.main.Image;
import com.example.wallpapersapplication.network.ApiService;
import com.example.wallpapersapplication.network.NetworkBoundResource;
import com.example.wallpapersapplication.network.Resource;
import com.example.wallpapersapplication.network.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepository {

    private static String LOG_TAG = "Data repository tag";
    private static DataRepository instance;
    private DatabaseDao databaseDao;
    private AppExecutors appExecutors;
    private ApiService service;

    public DataRepository(DatabaseDao databaseDao) {
        this.databaseDao = databaseDao;
        appExecutors = new AppExecutors();
        service = RestClient.getInstance().createService();
    }

    public static DataRepository getInstance(DatabaseDao databaseDao) {

        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(databaseDao);
                }
            }
        }
        return instance;
    }

    public LiveData<List<Image>> getImages(String categoryTitle) {
        return databaseDao.getImagesByCategory(categoryTitle);
    }

    public void updateImages(ArrayList<Image> images) {
        appExecutors.diskIO().execute(() -> {
            databaseDao.updateImages(images.toArray(new Image[0]));
        });
    }

    public void updateImage(Image image) {
        appExecutors.diskIO().execute(() -> {
            databaseDao.updateImage(image);
        });
    }

    public LiveData<Image> getSingleImage(int imageId) {
        return databaseDao.getSingleImage(imageId);
    }

    public LiveData<List<Image>> getFavoriteImages() {
        return databaseDao.getFavoriteImages();
    }

    public void removeDownloadedImage(Context context, Image image) {
        appExecutors.diskIO().execute(() -> {
            Utils.removeDownloadedImage(context, image);
        });
    }

    public void saveImage(Context context, Image image, Bitmap bitmap) {
        appExecutors.diskIO().execute(() -> {
            Utils.saveImage(context, image, bitmap);
        });
    }

    public LiveData<List<Image>> getAllImages() {
        return databaseDao.getAllImages();
    }

    /**
     * Get up to date downloaded image list
     *
     * @param albumName of the pictures in the external storage
     * @param images    parameter of the images saved at the local database
     * @return up to date downloaded image list
     */
    public ArrayList<Pair<Image, Bitmap>> getDownloadedImages(String albumName, List<Image> images) {
        Pair<ArrayList<Pair<Image, Bitmap>>, ArrayList<Image>> pair = Utils.getImages(albumName, images);
        appExecutors.diskIO().execute(() -> {
            databaseDao.updateImages(pair.second);
        });

        return pair.first;
    }

    public MutableLiveData<Resource<ArrayList<Image>>> getImageUrls(ArrayList<String> categoryTitles) {
        MutableLiveData<Resource<ArrayList<Image>>> liveData;
        liveData = new NetworkBoundResource<ArrayList<Image>>() {
            @Override
            protected void createCall() {
                service.getImageUrlFile().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BufferedReader r = new BufferedReader(response.body().charStream());
                            ArrayList<Image> urlList = new ArrayList<>();

                            String line = "";
                            while (line != null) {
                                try {
                                    line = r.readLine();
                                    // add categories
                                    if (line != null) {
                                        for (String title : categoryTitles) {
                                            if (line.startsWith(title)) {
                                                urlList.add(new Image(title, line, false, false));
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    Log.e("url read error:", e.toString());
                                }
                            }
                            setResultValue(Resource.success(urlList));
                        } else {
                            setResultValue(Resource.error(response.message(), null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        setResultValue(Resource.error(t.getMessage(), null));
                    }
                });
            }
        }.getAsMutableLiveData();

        return liveData;
    }
}


