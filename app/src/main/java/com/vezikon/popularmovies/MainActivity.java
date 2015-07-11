package com.vezikon.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.vezikon.popularmovies.fragments.MovieDetailFragment;
import com.vezikon.popularmovies.fragments.MoviesFragment;
import com.vezikon.popularmovies.models.Movie;

import static com.vezikon.popularmovies.fragments.MoviesFragment.*;


public class MainActivity extends AppCompatActivity implements OnMoviesFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment movieFragment = MoviesFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, movieFragment)
                .commit();
    }


    @Override
    public void onMovieSelected(Movie movie) {

        Fragment movieDetailFragment = MovieDetailFragment.newInstance(movie);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, movieDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
