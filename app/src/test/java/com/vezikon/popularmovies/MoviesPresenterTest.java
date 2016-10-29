package com.vezikon.popularmovies;

import android.support.v4.app.LoaderManager;

import com.vezikon.popularmovies.data.Movie;
import com.vezikon.popularmovies.data.Movies;
import com.vezikon.popularmovies.data.source.LoaderProvider;
import com.vezikon.popularmovies.data.source.MoviesRepository;
import com.vezikon.popularmovies.movies.MoviesContract;
import com.vezikon.popularmovies.movies.MoviesPresenter;
import com.vezikon.popularmovies.utils.schedulers.BaseSchedulerProvider;
import com.vezikon.popularmovies.utils.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by vezikon on 10/29/16.
 */

public class MoviesPresenterTest {

    private static  List<Movie> TASKS = new ArrayList<>();

    private static Movies movies = new Movies();

    @Mock
    private MoviesRepository moviesRepository;

    @Mock
    private MoviesContract.View moviesView;

    @Mock
    private LoaderManager loaderManager;

    private BaseSchedulerProvider mSchedulerProvider;

    private MoviesPresenter moviesPresenter;

    @Mock
    private LoaderProvider loaderProvider;

    @Before
    public void setupTasksPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Make the sure that all schedulers are immediate.
        mSchedulerProvider = new ImmediateSchedulerProvider();

        // Get a reference to the class under test
        moviesPresenter = new MoviesPresenter(moviesRepository, loaderProvider,
                loaderManager, mSchedulerProvider, moviesView);

        for(int i = 0 ; i < 3 ; i++){
            Movie movie = new Movie();
            movie.setId(1);

            TASKS.add(movie);
        }

        movies.setResults(TASKS);
    }

    @Test
    public void loadMoviesFromServer() {
        // Given an initialized TasksPresenter with initialized tasks
        when(moviesRepository.getMovies("")).thenReturn(Observable.just(movies));

        //when getting movies is requested
        moviesPresenter.getMovies("");

        verify(moviesView).showProgress(true);
        // Then progress indicator is hidden and all tasks are shown in UI
        verify(moviesView).showProgress(false);
    }
}
