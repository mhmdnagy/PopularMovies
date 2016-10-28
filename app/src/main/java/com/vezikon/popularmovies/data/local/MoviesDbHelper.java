package com.vezikon.popularmovies.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vezikon on 8/19/15.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " + MoviesContract.FavMoviesEntry.TABLE_NAME
                + " ("
                + MoviesContract.FavMoviesEntry._ID + " INTEGER PRIMARY KEY, "
                + MoviesContract.FavMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MoviesContract.FavMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + MoviesContract.FavMoviesEntry.COLUMN_POSTER_PATH + " TEXT, "
                + MoviesContract.FavMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MoviesContract.FavMoviesEntry.COLUMN_COUNT + " INTEGER NOT NULL, "
                + MoviesContract.FavMoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL "
                + " );";

        db.execSQL(SQL_CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavMoviesEntry.TABLE_NAME);

        onCreate(db);
    }
}
