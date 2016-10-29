package com.vezikon.popularmovies.movies;

import com.vezikon.popularmovies.BasePresenter;
import com.vezikon.popularmovies.BaseView;
import com.vezikon.popularmovies.data.Movie;

import java.util.ArrayList;

/**
 * Created by vezikon on 10/29/16.
 */

public interface MoviesContract {

    interface Presenter extends BasePresenter {
        void getMovies(String sortOptions);

        void showFavMovies();

        boolean isEmpty();

    }

    interface View extends BaseView<Presenter> {

        void showProgress(boolean show);

        void showMovies(ArrayList<Movie> movies);

        void selectMovie(int index);
    }
}
