package com.wahidhidayat.newsapp.rests;

import com.wahidhidayat.newsapp.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static APIClient apiClient;
    private static Retrofit retrofit = null;

    // mapping JSON data to Java Object
    private APIClient() {
        retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized APIClient getInstance() {
        if (apiClient == null) {
            apiClient = new APIClient();
        }
        return apiClient;
    }

    public APIInterface getApi() {
        return retrofit.create(APIInterface.class);
    }
}
