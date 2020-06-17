package com.wahidhidayat.newsapp.rests;

import com.wahidhidayat.newsapp.models.Headlines;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("top-headlines")
    Call<Headlines> getHeadlines(@Query("country") String country, @Query("apiKey") String apiKey);
}
