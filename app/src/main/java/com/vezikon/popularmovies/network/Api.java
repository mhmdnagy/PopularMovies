package com.vezikon.popularmovies.network;


import com.vezikon.popularmovies.data.Movies;
import com.vezikon.popularmovies.data.Reviews;
import com.vezikon.popularmovies.data.Trailers;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


/**
 * Created by vezikon on 1/28/15.
 */
public interface Api {

    @GET("/3/discover/movie")
    Observable<Movies> movies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/trailers")
    Observable<Trailers> trailers(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Observable<Reviews> reviews(@Path("id") String id, @Query("api_key") String apiKey);
}
