package com.vezikon.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.vezikon.popularmovies.data.source.LoaderProvider;
import com.vezikon.popularmovies.data.source.MoviesRepository;
import com.vezikon.popularmovies.data.source.local.MoviesContract;
import com.vezikon.popularmovies.data.source.local.MoviesLocalDataSource;
import com.vezikon.popularmovies.data.source.remote.MoviesRemoteDataSource;
import com.vezikon.popularmovies.moviedetails.MovieDetailFragment;
import com.vezikon.popularmovies.moviedetails.MoviePresenter;
import com.vezikon.popularmovies.movies.MoviesFragment;
import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.movies.MoviesPresenter;
import com.vezikon.popularmovies.utils.ActivityUtil;
import com.vezikon.popularmovies.utils.schedulers.SchedulerProvider;

import static com.vezikon.popularmovies.movies.MoviesFragment.*;


public class MainActivity extends AppCompatActivity implements OnMoviesFragmentListener {

    MoviesPresenter moviesPresenter;
    MoviePresenter moviePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesFragment movieFragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (movieFragment == null) {
            movieFragment = MoviesFragment.newInstance();
            ActivityUtil.addFirstFragmentToActivity(getSupportFragmentManager(), movieFragment, R.id.fragment);
        }

        LoaderProvider loaderProvider = new LoaderProvider(this);

        moviesPresenter = new MoviesPresenter(MoviesRepository.getInstance(MoviesLocalDataSource.getInstance(getContentResolver()),
                MoviesRemoteDataSource.getInstance()),
                loaderProvider,
                getSupportLoaderManager(),
                SchedulerProvider.getInstance(),
                movieFragment
        );
    }


    @Override
    public void onMovieSelected(Movie movie) {

        //check screen status
        if (getResources().getBoolean(R.bool.isMultiPane)) {
            addMultiPaneMovieDetailFragment(movie);
        } else {
            addStandaloneMovieDetailFragment(movie);
        }
    }

    private void addMultiPaneMovieDetailFragment(Movie movie) {
        MovieDetailFragment movieDetailFragment =
                MovieDetailFragment.newInstance(isFav(movie));
        ActivityUtil
                .addFragmentToActivity(getSupportFragmentManager(), movieDetailFragment, R.id.fragment_details);

        moviePresenter = new MoviePresenter(MoviesRepository.getInstance(MoviesLocalDataSource.getInstance(getContentResolver()),
                MoviesRemoteDataSource.getInstance()),
                SchedulerProvider.getInstance(),
                movie,
                movieDetailFragment
        );

    }

    private void addStandaloneMovieDetailFragment(Movie movie) {
        MovieDetailFragment movieDetailFragment =
                MovieDetailFragment.newInstance(isFav(movie));
        ActivityUtil
                .addFragmentToActivity(getSupportFragmentManager(), movieDetailFragment, R.id.fragment);

        moviePresenter = new MoviePresenter(MoviesRepository.getInstance(MoviesLocalDataSource.getInstance(getContentResolver()),
                MoviesRemoteDataSource.getInstance()),
                SchedulerProvider.getInstance(),
                movie,
                movieDetailFragment
        );
    }

    /**
     * Use this method to check whether the movie is in Favorite database or not
     *
     * @param movie instance of {@link Movie}
     * @return boolean
     */
    private boolean isFav(Movie movie) {

        String selection = MoviesContract.FavMoviesEntry.TABLE_NAME + "."
                + MoviesContract.FavMoviesEntry._ID + " = ? ";

        String[] selectionArgs = {String.valueOf(movie.getId())};

        Cursor cursor = getContentResolver().query(MoviesContract.FavMoviesEntry.CONTENT_URI,
                MoviesContract.FavMoviesEntry.MOVIE_COLUMNS,
                selection,
                selectionArgs,
                null
        );

        return cursor.getCount() > 0;
    }
}
