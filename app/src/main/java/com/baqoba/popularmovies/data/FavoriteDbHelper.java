package com.baqoba.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Admin on 08/08/2017.
 */

public class FavoriteDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "favorite.db";
    private static final int DATABASE_VERSION = 3;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_FAVORITE_TABLE =

                "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME
                        + " (" +
                        FavoriteContract.FavoriteEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID    + " INTEGER NOT NULL , "                 +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT, " +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, " +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW + " TEXT, " +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT, " +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT" +
                        FavoriteContract.FavoriteEntry.COLUMN_MOVIE_IS_FAVORITE + " TEXT NOT NULL " +
                        " );";
        Log.d("query_string ", SQL_CREATE_FAVORITE_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

