package com.vezikon.popularmovies.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vezikon.popularmovies.R;
import com.vezikon.popularmovies.Utils;
import com.vezikon.popularmovies.models.Movie;
import com.vezikon.popularmovies.models.Movies;
import com.vezikon.popularmovies.rest.RestClient;
import com.vezikon.popularmovies.views.adapters.MoviesAdapter;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.vezikon.popularmovies.data.MoviesContract.*;

/**
 * A fragment representing a list of Items.
 * <p>
 * <p>
 * Activities containing this fragment MUST implement the {@link OnMoviesFragmentListener}
 * interface.
 */
public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //UI
    @InjectView(android.R.id.list)
    GridView mGridView;
    @InjectView(R.id.progress_layout)
    ProgressBar progressLayout;


    private ArrayList<Movie> moviesList = new ArrayList<>();

    //constants
    public static final String API_KEY = "";
    private static final String TYPE_HIGHEST_RATE = "vote_average.desc";
    private static final String TYPE_MOST_POPULAR = "popularity.desc";
    private static final String TYPE_FAV = "fav";
    private static final int LOADER_ID = 0;
    private static final String KEY_LOADER_FLAG = "flag.loader";


    private MoviesAdapter adapter;

    private OnMoviesFragmentListener mListener;

    private static final String KEY_MOVIES = "movies.list";
    private static final String KEY_PREF = "movies.prefs";
    private static final String KEY_MOVIES_TYPE = "movies.type";

    //flags
    private boolean loaderFlag = false;

    //projections
    public static final String[] MOVIE_COLUMNS = {
            FavMoviesEntry.TABLE_NAME + "." + FavMoviesEntry._ID,
            FavMoviesEntry.COLUMN_TITLE,
            FavMoviesEntry.COLUMN_COUNT,
            FavMoviesEntry.COLUMN_RELEASE_DATE,
            FavMoviesEntry.COLUMN_VOTE_AVERAGE,
            FavMoviesEntry.COLUMN_OVERVIEW,
            FavMoviesEntry.COLUMN_POSTER_PATH,
    };

    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_COUNT = 2;
    private static final int COL_RELEASE_DATE = 3;
    private static final int COL_VOTE_AVERAGE = 4;
    private static final int COL_OVERVIEW = 5;
    private static final int COL_POSTER_PATH = 6;

    public static MoviesFragment newInstance() {
        MoviesFragment fragment = new MoviesFragment();

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            moviesList = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
            loaderFlag = savedInstanceState.getBoolean(KEY_LOADER_FLAG);
        }


        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movies, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        adapter = new MoviesAdapter(getActivity(), moviesList);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(this);

        if (moviesList.isEmpty()) {

            //getting saved type
            SharedPreferences preferences =
                    getActivity().getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
            String type = preferences.getString(KEY_MOVIES_TYPE, TYPE_MOST_POPULAR);

            if (!type.equalsIgnoreCase(TYPE_FAV)) {

                //checking network state
                if (Utils.isNetworkAvailable(getActivity())) {
                    //showing progress view
                    showProgress(true);

                    //getting data from the internet
                    getMovies(type);

                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_msg_no_connection), Toast.LENGTH_LONG).show();
                }

            } else {
                //start loader to get offline favorites
                getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            }
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMoviesFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMoviesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        //getting saved type
        SharedPreferences preferences =
                getActivity().getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        String type = preferences.getString(KEY_MOVIES_TYPE, TYPE_MOST_POPULAR);

        //restart loader to update content provider changes
        if (type.equalsIgnoreCase(TYPE_FAV) && loaderFlag) {
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        loaderFlag = true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface and send the activity the movies
            // object to be viewed in the MovieDetailsFragment
            mListener.onMovieSelected(moviesList.get(position));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity()
                , FavMoviesEntry.CONTENT_URI
                , MOVIE_COLUMNS
                , null
                , null
                , null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        moviesList.clear();

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

            //notify the adapter with data changes
            adapter.notifyDataSetChanged();


            if (getResources().getBoolean(R.bool.isMultiPane)
                    && mListener != null
                    && moviesList.size() > 0) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onMovieSelected(moviesList.get(0));

                    }
                });
            }

        }


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMoviesFragmentListener {
        // TODO: Update argument type and name
        public void onMovieSelected(Movie movie);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        SharedPreferences preferences = getActivity().getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        switch (id) {
            case R.id.action_most_popular:

                showProgress(true);

                //saving data
                editor.putString(KEY_MOVIES_TYPE, TYPE_MOST_POPULAR);
                editor.apply();

                getMovies(TYPE_MOST_POPULAR);
                break;

            case R.id.action_highest_rate:

                showProgress(true);

                //saving data
                editor.putString(KEY_MOVIES_TYPE, TYPE_HIGHEST_RATE);
                editor.apply();

                getMovies(TYPE_HIGHEST_RATE);
                break;
            case R.id.action_fav_movies:
                //saving data
                editor.putString(KEY_MOVIES_TYPE, TYPE_FAV);
                editor.apply();


                if (getActivity().getSupportLoaderManager().getLoader(LOADER_ID) != null) {
                    //restart loader to update content provider changes
                    getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
                    Log.d("restarting", "loader");
                } else {
                    getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
                    Log.d("new", "loader");

                }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * this method is responsible for getting movies list from the backend
     *
     * @param sortOptions sorting option whether sorting by most popular or highest rate
     */
    private void getMovies(String sortOptions) {

        RestClient.get().movies(sortOptions, API_KEY, new Callback<Movies>() {
            @Override
            public void success(Movies movies, Response response) {

                Log.d("movies size", movies.getResults().size() + "");

                if (moviesList.isEmpty()) {
                    moviesList.addAll(movies.getResults());
                } else {
                    //clear the current movies list before adding the new data
                    moviesList.clear();
                    moviesList.addAll(movies.getResults());
                }


                //notify the adapter with data changes
                adapter.notifyDataSetChanged();

                //turn off the progress view
                showProgress(false);

                if (getResources().getBoolean(R.bool.isMultiPane) && mListener != null) {
                    mListener.onMovieSelected(moviesList.get(0));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(),
                        getString(R.string.error_msg_failed_to_load), Toast.LENGTH_SHORT).show();

                showProgress(false);
            }
        });
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mGridView.setVisibility(show ? View.GONE : View.VISIBLE);
            mGridView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mGridView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            progressLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            mGridView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(KEY_MOVIES, moviesList);
        outState.putBoolean(KEY_LOADER_FLAG, loaderFlag);
    }
}
