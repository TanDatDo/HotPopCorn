package com.dan.hotpopcorn.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dat T Do on 9/20/2017.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.dan.hotpopcorn";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "movies";

    /**
     * Created by Dat T Do on 7/20/2017.
     */


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MovieContract() {
    }

    /**
     * Inner class that defines constant values for the movie database table.
     * Each entry in the table represents a single movie.
     */
    public static final class MovieEntry implements BaseColumns {

        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of movies.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single movie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        /**
         * Name of database table for movies
         */
        public final static String TABLE_NAME = "movies";

        /**
         * Unique ID number for the movie (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Column containing information about movies such as: title, poster, backdrop...
         */
        public final static String COLUMN_MOVIE_TITLE = "title";
        public final static String COLUMN_MOVIE_POSTER = "poster";
        public final static String COLUMN_MOVIE_BACKDROP = "backdrop";
        public final static String COLUMN_MOVIE_DATE_RELEASE = "release_date";
        public final static String COLUMN_MOVIE_OVERVIEW = "overview";
        public final static String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public final static String COLUMN_MOVIE_ID = "id";
        public final static String COLUMN_MOVIE_BACKDROP_URL = "backdrop_url";
        public final static String COLUMN_MOVIE_POSTER_URL = "poster_url";
    }
}


