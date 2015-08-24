package com.vezikon.popularmovies.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vezikon.popularmovies.R;
import com.vezikon.popularmovies.Utils;
import com.vezikon.popularmovies.data.MoviesContract;
import com.vezikon.popularmovies.models.Movie;
import com.vezikon.popularmovies.models.Review;
import com.vezikon.popularmovies.models.Reviews;
import com.vezikon.popularmovies.models.Trailer;
import com.vezikon.popularmovies.models.Trailers;
import com.vezikon.popularmovies.rest.RestClient;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.LinearLayout.*;
import static com.vezikon.popularmovies.data.MoviesContract.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE = "movie";
    private static final String ARG_IS_FAV = "is.fav";

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
    @InjectView(R.id.container)
    LinearLayout container;

    private Movie movie;
    private boolean isFav;

    private static final String KEY_MOVIE = "movie";
    private static final String KEY_IS_FAV = "is.fav";

    private final String TAG = this.getClass().getName();
    private final String YOUTUBE = "http://img.youtube.com/vi/";

    private ArrayList<Trailer> trailersList = new ArrayList<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie movie instance.
     * @return A new instance of fragment MovieDetailFragment.
     */
    public static MovieDetailFragment newInstance(Movie movie, boolean isFav) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        args.putBoolean(ARG_IS_FAV, isFav);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);
            isFav = getArguments().getBoolean(ARG_IS_FAV);

            //no need to continue the method, we already got some data
            return;
        }

        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(KEY_MOVIE);
            isFav = savedInstanceState.getBoolean(KEY_IS_FAV);

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE, movie);
        outState.putBoolean(KEY_IS_FAV, isFav);
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

        if (movie.getPoster_path() != null)
            Picasso.with(getActivity()).load(path + movie.getPoster_path())
                    .placeholder(R.drawable.placeholder)
                    .into(img);


        if (Utils.isNetworkAvailable(getActivity())) {
            //getting trailers
            getTrailers(movie.getId());
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_msg_no_connection), Toast.LENGTH_LONG).show();
        }

    }

    private void getTrailers(final int movieId) {
        RestClient.get().trailers(movieId + "", MoviesFragment.API_KEY, new Callback<Trailers>() {
            @Override
            public void success(Trailers response, Response response2) {
                if (response.getYoutube().size() > 0) {

                    trailersList = response.getYoutube();

                    //add section title
                    addTitle(getString(R.string.movie));
                    //add content
                    addTrailers(response.getYoutube());
                }

                //getting reviews
                getReviews(movie.getId());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.toString());

                //getting reviews
                getReviews(movie.getId());
            }
        });
    }


    private void getReviews(int movieId) {
        RestClient.get().reviews(movieId + "", MoviesFragment.API_KEY, new Callback<Reviews>() {
            @Override
            public void success(Reviews reviews, Response response) {
                if (reviews.getResults().size() > 0) {
                    addTitle(getString(R.string.reviews));
                    addReview(reviews.getResults());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.toString());
            }
        });
    }

    /**
     * Use this method to add trailers thumbnails to the views' container
     *
     * @param trailerArrayList list of trailers
     */
    private void addTrailers(ArrayList<Trailer> trailerArrayList) {

        for (final Trailer trailer : trailerArrayList) {
            ImageView imageView = new ImageView(getActivity());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            imageView.setPadding(10, 10, 10, 10);

            imageView.setLayoutParams(params);

            //getting video thumbnail
            Picasso.with(getActivity()).load(YOUTUBE + trailer.getSource() + "/0.jpg")
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=" + trailer.getSource()));
                    startActivity(intent);
                }
            });

            if (container != null)
                container.addView(imageView);
        }


    }

    /**
     * Use this method to add reviews to the views' container
     *
     * @param reviewsList list of reviews
     */
    private void addReview(ArrayList<Review> reviewsList) {

        View view;

        for (Review review : reviewsList) {

            view = LayoutInflater.from(getActivity()).inflate(R.layout.item_review, null);

            TextView author = (TextView) view.findViewById(R.id.author);
            TextView content = (TextView) view.findViewById(R.id.content);

            author.setText(review.getAuthor());
            content.setText(review.getContent());

            if (container != null)
                container.addView(view);
        }


    }

    /**
     * Use this method to add title banner to the views' container
     *
     * @param title the title of the banner
     */
    private void addTitle(String title) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_title, null);

        TextView textView = (TextView) view.findViewById(R.id.title_text);
        textView.setText(title);

        if (container != null)
            container.addView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.fragment_movie_details_menu, menu);

        MenuItem item = menu.findItem(R.id.action_fav);


        item.setChecked(isFav);
        item.setIcon(item.isChecked() ? R.drawable.ic_favorite_white_18dp : R.drawable.ic_favorite_outline_white_18dp);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_fav:
                item.setChecked(!item.isChecked());
                item.setIcon(item.isChecked() ? R.drawable.ic_favorite_white_18dp : R.drawable.ic_favorite_outline_white_18dp);

                if (item.isChecked()) {
                    addToFav(movie);
                } else {
                    Log.d("removing", "called");
                    removeFromFav(movie);
                }
                break;
            case R.id.share:

                if (trailersList.size() > 0) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "https://www.youtube.com/watch?v=" + trailersList.get(0).getSource());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } else {
                    Toast.makeText(getActivity(), "No trailers to share", Toast.LENGTH_SHORT).show();
                }

                break;


        }

        return super.onOptionsItemSelected(item);
    }


    private void addToFav(Movie movie) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavMoviesEntry._ID, movie.getId());
        contentValues.put(FavMoviesEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(FavMoviesEntry.COLUMN_COUNT, movie.getVote_count());
        contentValues.put(FavMoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(FavMoviesEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        contentValues.put(FavMoviesEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        contentValues.put(FavMoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());

        getActivity().getContentResolver().insert(FavMoviesEntry.CONTENT_URI, contentValues);

    }

    private void removeFromFav(Movie movie) {

        String selection = FavMoviesEntry._ID + " = ? ";
        String[] selectionArgs = {movie.getId() + ""};

        getActivity().getContentResolver().delete(FavMoviesEntry.CONTENT_URI, selection, selectionArgs);
    }
}
