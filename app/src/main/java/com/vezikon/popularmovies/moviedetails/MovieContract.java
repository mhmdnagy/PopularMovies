package com.vezikon.popularmovies.moviedetails;

import com.vezikon.popularmovies.BasePresenter;
import com.vezikon.popularmovies.BaseView;
import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Review;
import com.vezikon.popularmovies.data.Trailer;

import java.util.ArrayList;

/**
 * Created by vezikon on 10/29/16.
 */

public interface MovieContract {

    interface Presenter extends BasePresenter {
        void getTrailers(int movieId);

        void getReviews(int movieId);

        void addToFav();

        void removeFromFav();
    }

    interface View extends BaseView<Presenter> {
        void showTrailers(ArrayList<Trailer> trailers);

        void showReviews(ArrayList<Review> reviews);

        void showMovieDetail(Movie movie);

    }
}
