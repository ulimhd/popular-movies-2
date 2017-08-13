package com.baqoba.popularmovies.utilities;

import android.content.Context;
import android.database.Cursor;

import com.baqoba.popularmovies.data.FavoriteContract;

/**
 * Created by ulimhd on 08/08/17.
 */

public class Utility {
    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,   // projection
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[] { Integer.toString(id) },   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String buildImageUrl(int width, String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }
}
