package com.vezikon.popularmovies.moviedetails;

import android.support.annotation.NonNull;
import android.util.Log;

import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Reviews;
import com.vezikon.popularmovies.data.Trailers;
import com.vezikon.popularmovies.data.source.MoviesRepository;
import com.vezikon.popularmovies.utils.schedulers.BaseSchedulerProvider;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vezikon on 10/29/16.
 */

public class MoviePresenter implements MovieContract.Presenter {

    private final static String TAG = "MoviePresenter";

    @NonNull
    private MoviesRepository moviesRepository;

    @NonNull
    BaseSchedulerProvider schedulerProvider;

    @NonNull
    Movie movie;

    CompositeSubscription subscription;

    @NonNull
    private MovieContract.View movieView;

    public MoviePresenter(@NonNull MoviesRepository moviesRepository,
                          @NonNull BaseSchedulerProvider schedulerProvider,
                          @NonNull Movie movie,
                          @NonNull MovieContract.View movieView) {
        this.moviesRepository = checkNotNull(moviesRepository);
        this.schedulerProvider = checkNotNull(schedulerProvider);
        this.movieView = checkNotNull(movieView);
        this.movie = checkNotNull(movie);

        this.movieView.setPresenter(this);

        subscription = new CompositeSubscription();
    }

    @Override
    public void getTrailers(int movieId) {

        Subscription subscription = moviesRepository.getTrailers(String.valueOf(movieId))
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.computation())
                .subscribe(new Subscriber<Trailers>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error - " + e.toString());
                    }

                    @Override
                    public void onNext(Trailers trailers) {
                        if (!trailers.getYoutube().isEmpty())
                            movieView.showTrailers(trailers.getYoutube());
                    }
                });

        this.subscription.add(subscription);
    }

    @Override
    public void getReviews(int movieId) {

        Subscription subscription = moviesRepository.getReviews(String.valueOf(movieId))
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.computation())
                .subscribe(new Subscriber<Reviews>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error - " + e.toString());
                    }

                    @Override
                    public void onNext(Reviews reviews) {
                        if (!reviews.getResults().isEmpty())
                            movieView.showReviews(reviews.getResults());
                    }
                });

        this.subscription.add(subscription);
    }

    @Override
    public void addToFav() {
        moviesRepository.addToFav(movie);
    }

    @Override
    public void removeFromFav() {
        moviesRepository.removeFromFav(movie);
    }

    @Override
    public void subscribe() {
        movieView.showMovieDetail(movie);
    }

    @Override
    public void unsubscribe() {
        subscription.clear();
    }
}
