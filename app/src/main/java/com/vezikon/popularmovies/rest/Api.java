package com.vezikon.popularmovies.rest;


import com.vezikon.popularmovies.models.Movies;

import retrofit.Callback;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by vezikon on 1/28/15.
 */
public interface Api {

    @GET("/discover/movie")
    void movies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey,
                Callback<Movies> callback);
}
