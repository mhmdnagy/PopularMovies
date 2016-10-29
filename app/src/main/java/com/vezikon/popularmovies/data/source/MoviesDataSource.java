package com.vezikon.popularmovies.data.source;

import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Movies;
import com.vezikon.popularmovies.data.Reviews;
import com.vezikon.popularmovies.data.Trailers;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by vezikon on 10/29/16.
 */

public interface MoviesDataSource {

    Observable<Movies> getMovies(String sortBy);

    Observable<Movies> getFavMovies();

    Observable<Trailers> getTrailers(String id);

    Observable<Reviews> getReviews(String id);

    void addToFav(Movie movie);

    void removeFromFav(Movie movie);

    void setMovies(ArrayList<Movie> movies);

    ArrayList<Movie> getMovies();

}
