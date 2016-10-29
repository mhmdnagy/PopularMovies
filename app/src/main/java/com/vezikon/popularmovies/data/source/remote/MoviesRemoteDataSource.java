package com.vezikon.popularmovies.data.source.remote;

import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Movies;
import com.vezikon.popularmovies.data.Reviews;
import com.vezikon.popularmovies.data.Trailers;
import com.vezikon.popularmovies.data.source.MoviesDataSource;
import com.vezikon.popularmovies.network.RestClient;
import com.vezikon.popularmovies.utils.ApiKey;

import rx.Observable;

/**
 * Created by vezikon on 10/29/16.
 */
public class MoviesRemoteDataSource implements MoviesDataSource {

    private static MoviesRemoteDataSource INSTANCE;

    public static MoviesRemoteDataSource getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MoviesRemoteDataSource();
        return INSTANCE;
    }

    private MoviesRemoteDataSource() {
    }

    @Override
    public Observable<Movies> getMovies(String sortBy) {
        return RestClient.get().movies(sortBy, ApiKey.getApiKey());
    }

    @Override
    public Observable<Movies> getFavMovies() {
        return null;
    }

    @Override
    public Observable<Trailers> getTrailers(String id) {
        return RestClient.get().trailers(id, ApiKey.getApiKey());
    }

    @Override
    public Observable<Reviews> getReviews(String id) {
        return RestClient.get().reviews(id, ApiKey.getApiKey());
    }

    @Override
    public void addToFav(Movie movie) {

    }

    @Override
    public void removeFromFav(Movie movie) {

    }
}
