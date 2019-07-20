package com.example.wallpapersapplication.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("tree.txt")
    Call<ResponseBody> getImageUrlFile();
}
