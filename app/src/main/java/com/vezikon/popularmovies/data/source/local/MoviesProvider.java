package com.vezikon.popularmovies.data.source.local;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by vezikon on 8/19/15.
 */
public class MoviesProvider extends ContentProvider {

    public final static int FAV_MOVIES = 100;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    private MoviesDbHelper moviesDbHelper;

    static {

        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_FAV_MOVIES, FAV_MOVIES);
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        switch (matcher.match(uri)) {
            case FAV_MOVIES:
                cursor = moviesDbHelper.getReadableDatabase().query(MoviesContract.FavMoviesEntry.TABLE_NAME
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (matcher.match(uri)) {
            case FAV_MOVIES:
                return MoviesContract.FavMoviesEntry.CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = matcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAV_MOVIES: {
                long _id = db.insert(MoviesContract.FavMoviesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.FavMoviesEntry.buildFavMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new android.database.SQLException("Failed to insert row into " + uri);

        }

        //notify data change in the content provider
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = matcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case FAV_MOVIES:
                rowsDeleted = db.delete(
                        MoviesContract.FavMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            Log.d("content resolver", "notified");
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = matcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FAV_MOVIES:
                rowsUpdated = db.update(MoviesContract.FavMoviesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
