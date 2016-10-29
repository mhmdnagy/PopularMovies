package com.vezikon.popularmovies.data.source;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.vezikon.popularmovies.data.source.local.MoviesContract;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.vezikon.popularmovies.data.source.local.MoviesContract.FavMoviesEntry.MOVIE_COLUMNS;

/**
 * Created by vezikon on 10/29/16.
 */

public class LoaderProvider {

    @NonNull
    Context context;

    public LoaderProvider(@NonNull Context context) {
        this.context = checkNotNull(context, "Context cannot be null");
    }

    public Loader<Cursor> createFavMoviesLoader() {
        return new CursorLoader(context
                , MoviesContract.FavMoviesEntry.CONTENT_URI
                , MOVIE_COLUMNS
                , null
                , null
                , null);
    }
}
