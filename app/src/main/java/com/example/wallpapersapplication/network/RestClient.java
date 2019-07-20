package com.example.wallpapersapplication.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static final String BASE_URL = "http://167.71.12.90/";

    private volatile static RestClient instance = null;
    private Retrofit retrofit;

    private RestClient() {
    }

    public static RestClient getInstance() {

        if (instance == null) {
            synchronized (RestClient.class) {
                if (instance == null) {
                    instance = new RestClient();
                }
            }
        }
        return instance;
    }

    public ApiService createService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(ApiService.class);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
