package com.vezikon.popularmovies.movies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Movies;
import com.vezikon.popularmovies.data.source.LoaderProvider;
import com.vezikon.popularmovies.data.source.MoviesRepository;
import com.vezikon.popularmovies.utils.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vezikon on 10/29/16.
 */

public class MoviesPresenter implements MoviesContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private final static String TAG = "MoviesPresenter";
    public final static int MOVIES_LOADER = 1;

    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_COUNT = 2;
    private static final int COL_RELEASE_DATE = 3;
    private static final int COL_VOTE_AVERAGE = 4;
    private static final int COL_OVERVIEW = 5;
    private static final int COL_POSTER_PATH = 6;

    @NonNull
    private MoviesRepository moviesRepository;

    @NonNull
    private LoaderProvider loaderProvider;

    @NonNull
    private LoaderManager loaderManager;

    @NonNull
    BaseSchedulerProvider schedulerProvider;

    CompositeSubscription subscription;

    @NonNull
    private MoviesContract.View movieView;

    public MoviesPresenter(@NonNull MoviesRepository moviesRepository,
                           @NonNull LoaderProvider loaderProvider,
                           @NonNull LoaderManager loaderManager,
                           @NonNull BaseSchedulerProvider schedulerProvider,
                           @NonNull MoviesContract.View movieView) {
        this.moviesRepository = checkNotNull(moviesRepository);
        this.loaderProvider = checkNotNull(loaderProvider);
        this.loaderManager = checkNotNull(loaderManager);
        this.schedulerProvider = checkNotNull(schedulerProvider);
        this.movieView = checkNotNull(movieView);

        this.movieView.setPresenter(this);

        subscription = new CompositeSubscription();
    }

    @Override
    public void getMovies(String sortOptions) {

        movieView.showProgress(true);

        subscription.clear();
        Subscription subscription = moviesRepository.getMovies(sortOptions)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(new Subscriber<Movies>() {
                    @Override
                    public void onCompleted() {
                        movieView.showProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error - " + e.toString());
                    }

                    @Override
                    public void onNext(Movies movies) {
                        moviesRepository.setMovies((ArrayList<Movie>) movies.getResults());
                        movieView.showMovies((ArrayList<Movie>) movies.getResults());
                    }
                });

        this.subscription.add(subscription);
    }

    @Override
    public void showFavMovies() {

        if (loaderManager.getLoader(MOVIES_LOADER) != null) {
            //restart loader to update content provider changes
            loaderManager.restartLoader(MOVIES_LOADER, null, this);
            Log.d(TAG, "restarting loader");
        } else {
            loaderManager.initLoader(MOVIES_LOADER, null, this);
            Log.d(TAG, "init loader");
        }
    }

    @Override
    public boolean isEmpty() {
        return moviesRepository.getMovies().isEmpty();
    }

    @Override
    public Movie selectMovie(int index) {
        return moviesRepository.getMovies().get(index);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        subscription.clear();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return loaderProvider.createFavMoviesLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToLast()) {
                loadData(data);
            }
        }
    }

    private void loadData(Cursor data) {
        ArrayList<Movie> moviesList = new ArrayList<>();

        if (data.getCount() > 0) {
            data.moveToFirst();

            do {
                Movie movie = new Movie();
                movie.setId(data.getInt(COL_ID));
                movie.setTitle(data.getString(COL_TITLE));
                movie.setOverview(data.getString(COL_OVERVIEW));
                movie.setPoster_path(data.getString(COL_POSTER_PATH));
                movie.setVote_average(data.getDouble(COL_VOTE_AVERAGE));
                movie.setVote_count(data.getInt(COL_COUNT));
                movie.setRelease_date(data.getString(COL_RELEASE_DATE));

                moviesList.add(movie);

            } while (data.moveToNext());

            movieView.showMovies(moviesList);

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        loader.reset();
    }
}
