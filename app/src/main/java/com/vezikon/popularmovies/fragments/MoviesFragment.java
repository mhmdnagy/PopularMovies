package com.vezikon.popularmovies.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
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

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMoviesFragmentListener}
 * interface.
 */
public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener {

    //UI
    @InjectView(android.R.id.list)
    GridView mGridView;
    @InjectView(R.id.progress_layout)
    ProgressBar progressLayout;


    private ArrayList<Movie> moviesList = new ArrayList<>();


    private static final String API_KEY = "a491e06af68296a1fad86c70235e98f9";
    private static final String TYPE_HIGHEST_RATE = "vote_average.desc";
    private static final String TYPE_MOST_POPULAR = "popularity.desc";

    private MoviesAdapter adapter;

    private OnMoviesFragmentListener mListener;

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

        //checking network state
        if (Utils.isNetworkAvailable(getActivity())) {
            showProgress(true);
            getMovies(TYPE_MOST_POPULAR);
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_msg_no_connection), Toast.LENGTH_LONG).show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
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


        switch (id) {
            case R.id.action_most_popular:

                showProgress(true);
                getMovies(TYPE_MOST_POPULAR);
                break;
            case R.id.action_highest_rate:

                showProgress(true);
                getMovies(TYPE_HIGHEST_RATE);
                break;
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
}
