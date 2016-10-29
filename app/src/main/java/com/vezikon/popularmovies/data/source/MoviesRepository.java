package com.vezikon.popularmovies.data.source;

import android.support.annotation.NonNull;

import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Movies;
import com.vezikon.popularmovies.data.Reviews;
import com.vezikon.popularmovies.data.Trailers;
import com.vezikon.popularmovies.data.source.local.MoviesLocalDataSource;
import com.vezikon.popularmovies.data.source.remote.MoviesRemoteDataSource;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by vezikon on 10/29/16.
 */
public class MoviesRepository implements MoviesDataSource {

    private static MoviesRepository INSTANCE;

    private MoviesLocalDataSource moviesLocalDataSource;

    private MoviesRemoteDataSource moviesRemoteDataSource;


    public static MoviesRepository getInstance(@NonNull MoviesLocalDataSource moviesLocalDataSource,
                                               @NonNull MoviesRemoteDataSource moviesRemoteDataSource) {
        if (INSTANCE == null)
            INSTANCE = new MoviesRepository(moviesLocalDataSource, moviesRemoteDataSource);
        return INSTANCE;
    }

    private MoviesRepository(@NonNull MoviesLocalDataSource moviesLocalDataSource,
                             @NonNull MoviesRemoteDataSource moviesRemoteDataSource) {
        this.moviesRemoteDataSource = moviesRemoteDataSource;
        this.moviesLocalDataSource = moviesLocalDataSource;
    }


    @Override
    public Observable<Movies> getMovies(String sortBy) {
        return moviesRemoteDataSource.getMovies(sortBy);
    }

    @Override
    public Observable<Movies> getFavMovies() {
        return null;
    }

    @Override
    public Observable<Trailers> getTrailers(String id) {
        return moviesRemoteDataSource.getTrailers(id);
    }

    @Override
    public Observable<Reviews> getReviews(String id) {
        return moviesRemoteDataSource.getReviews(id);
    }

    @Override
    public void addToFav(Movie movie) {
        moviesLocalDataSource.addToFav(movie);
    }

    @Override
    public void removeFromFav(Movie movie) {
        moviesLocalDataSource.removeFromFav(movie);
    }

    @Override
    public void setMovies(ArrayList<Movie> movies) {
        moviesRemoteDataSource.setMovies(movies);
    }

    @Override
    public ArrayList<Movie> getMovies() {
        return moviesRemoteDataSource.getMovies();
    }
}
