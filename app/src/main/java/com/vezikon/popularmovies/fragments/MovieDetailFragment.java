package com.vezikon.popularmovies.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vezikon.popularmovies.R;
import com.vezikon.popularmovies.models.Movie;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE = "movie";
    @InjectView(R.id.movie_detail_title)
    TextView title;
    @InjectView(R.id.movie_detail_image)
    ImageView img;
    @InjectView(R.id.movie_detail_year)
    TextView year;
    @InjectView(R.id.movie_detail_duration)
    TextView duration;
    @InjectView(R.id.movie_detail_rate)
    TextView rate;
    @InjectView(R.id.movie_detail_plot)
    TextView plot;

    private Movie movie;

    private static final String KEY_MOVIE = "movie";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie movie instance.
     * @return A new instance of fragment MovieDetailFragment.
     */
    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);

            //no need to continue the method, we already got some data
            return;
        }

        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(KEY_MOVIE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE, movie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setting data
        title.setText(movie.getTitle());
        plot.setText(movie.getOverview());
        year.setText(movie.getRelease_date());
        rate.setText(movie.getVote_average() + "/10");

        String path = "http://image.tmdb.org/t/p/w185";

        Picasso.with(getActivity()).load(path + movie.getPoster_path())
                .placeholder(R.drawable.placeholder)
                .into(img);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
