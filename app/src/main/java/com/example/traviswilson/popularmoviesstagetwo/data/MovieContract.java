package com.example.traviswilson.popularmoviesstagetwo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by traviswilson on 12/13/16.
 */
public class MovieContract {
    //Name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.traviswilson.popularmoviesstagetwo.provider";
    //content authority used to create base for all URIs used to contact this movie database
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    // 2 paths for content provider
    public static final String PATH_MOVIES_POPULAR = "movies_popular";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_MOVIES_BEST = "movies_best";

    //inner class for movies info
    public static final class MoviePopularEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_MOVIES_POPULAR).build();
        //column names and information now
        static final String TABLE_NAME = "movies_popular";
        //remember that dir means directory and item means singleton
        //These are the types of content that we are going to return when queried
        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_POPULAR;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_POPULAR;
        //column names

        //file path for the image as stored elsewhere. This will be loaded using piccasso
        public static final String IMAGE_FILE = "image_file";
        //other self explanatory columns with text information
        public static final String SUMMARY = "summary";
        public static final String RELEASE_DATE = "release_date";
        public static final String TITLE = "title";
        //double rating
        public static final String RATING = "rating";
        //boolean adult or not
        public static final String ADULT_STATUS = "adult_status";
        public static final String MOVIE_ID = "movie_id";

        public static Uri buildPopularUri(long id){ //method used for the db.insert() to return Uri of rows
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
    public static final class MovieBestEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_MOVIES_BEST).build();
        //column names and information now
        public static final String TABLE_NAME = "movies_best";
        //remember that dir means directory and item means singleton
        //These are the types of content that we are going to return when queried
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_BEST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_BEST;
        //column names

        //file path for the image as stored elsewhere. This will be loaded using piccasso
        public static final String IMAGE_FILE = "image_file";
        //other self explanatory columns with text information
        public static final String SUMMARY = "summary";
        public static final String RELEASE_DATE = "release_date";
        public static final String TITLE = "title";
        //double rating
        public static final String RATING = "rating";
        //boolean adult or not
        public static final String ADULT_STATUS = "adult_status";
        public static final String MOVIE_ID = "movie_id";

        public static Uri buildBestUri(long id){ //method used for the db.insert() to return Uri of rows
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    //inner class for Favorites
    public static final class FavoritesEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME = "favorites";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        //column names

        //file path for the image as stored elsewhere. This will be loaded using piccasso
        public static final String IMAGE_FILE = "image_file";
        //other self explanatory columns with text information
        public static final String SUMMARY = "summary";
        public static final String RELEASE_DATE = "release_date";
        public static final String TITLE = "title";
        //double rating
        public static final String RATING = "rating";
        //boolean adult or not
        public static final String ADULT_STATUS = "adult_status";
        public static final String MOVIE_ID = "movie_id";

        public static Uri buildFavoriteUri(long id){ //method used for the db.insert() to return Uri of rows
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
