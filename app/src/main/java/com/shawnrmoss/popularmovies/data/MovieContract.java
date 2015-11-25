/**
 * Created by shawn on 11/24/2015.
 */

package com.shawnrmoss.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */
public class MovieContract {
    /*
        Inner class that defines the table contents of the movie table
     */
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "";
        public static final String COLUMN_RELEASE_DATE = "";
        public static final String COLUMN_POSTER_URL = "";
        public static final String COLUMN_BACKGROUND_URL = "";
        public static final String COLUMN_VOTE_AVERAGE = "";
        public static final String COLUMN_PLOT = "";
    }
}
