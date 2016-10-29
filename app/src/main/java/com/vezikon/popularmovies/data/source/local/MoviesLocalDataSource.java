package com.vezikon.popularmovies.data.source.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Movies;
import com.vezikon.popularmovies.data.Reviews;
import com.vezikon.popularmovies.data.Trailers;
import com.vezikon.popularmovies.data.source.MoviesDataSource;

import java.util.ArrayList;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vezikon on 10/29/16.
 */
public class MoviesLocalDataSource implements MoviesDataSource {

    private static MoviesLocalDataSource INSTANCE;

    @NonNull
    private ContentResolver contentResolver;

    public static MoviesLocalDataSource getInstance(@NonNull ContentResolver contentResolver) {
        if (INSTANCE == null)
            INSTANCE = new MoviesLocalDataSource(contentResolver);
        return INSTANCE;
    }

    private MoviesLocalDataSource(@NonNull ContentResolver contentResolver) {
        this.contentResolver = checkNotNull(contentResolver, "ContentResolver cannot be null");
    }

    @Override
    public void addToFav(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.FavMoviesEntry._ID, movie.getId());
        contentValues.put(MoviesContract.FavMoviesEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.FavMoviesEntry.COLUMN_COUNT, movie.getVote_count());
        contentValues.put(MoviesContract.FavMoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MoviesContract.FavMoviesEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        contentValues.put(MoviesContract.FavMoviesEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        contentValues.put(MoviesContract.FavMoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());

        contentResolver.insert(MoviesContract.FavMoviesEntry.CONTENT_URI, contentValues);
    }

    @Override
    public void removeFromFav(Movie movie) {

        String selection = MoviesContract.FavMoviesEntry._ID + " = ? ";
        String[] selectionArgs = {movie.getId() + ""};

        contentResolver.delete(MoviesContract.FavMoviesEntry.CONTENT_URI, selection, selectionArgs);
    }

    @Override
    public void setMovies(ArrayList<Movie> movies) {
        // no-op since the data is loader via Cursor Loader
    }

    @Override
    public ArrayList<Movie> getMovies() {
        // no-op since the data is loader via Cursor Loader
        return null;
    }

    @Override
    public Observable<Movies> getMovies(String sortBy) {
        return null;
    }

    @Override
    public Observable<Movies> getFavMovies() {
        // no-op since the data is loader via Cursor Loader
        return null;
    }

    @Override
    public Observable<Trailers> getTrailers(String id) {
        return null;
    }

    @Override
    public Observable<Reviews> getReviews(String id) {
        return null;
    }
}
