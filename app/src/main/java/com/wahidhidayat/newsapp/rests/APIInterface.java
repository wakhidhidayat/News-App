package com.wahidhidayat.newsapp.rests;

import com.wahidhidayat.newsapp.models.Headlines;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    // endpoint to get headlines
    @GET("top-headlines")
    Call<Headlines> getHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pages,
            @Query("category") String category
    );

    // endpoint to get search
    @GET("everything")
    Call<Headlines> getNews(@Query("q") String query, @Query("apiKey") String apiKey, @Query("pageSize") int pages);
}