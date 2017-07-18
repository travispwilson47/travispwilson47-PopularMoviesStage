package com.example.traviswilson.popularmoviesstagetwo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by traviswilson on 12/13/16.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieDbHelper movieDbHelper;

    //constants for the uri matching for TABLE acquisition in queries and such
    public static final int ALL_MOVIE_BEST = 0;
    public static final int ONE_MOVIE_BEST = 1;
    public static final int ALL_MOVIE_POPULAR = 2;
    public static final int ONE_MOVIE_POPULAR = 3;
    public static final int ALL_FAVORITES = 4;
    public static final int ONE_FAVORITE = 5;


    //here is the point where we add the URI's we are supporting
    //remember that this is the actual tables that we are looking at
    //we are only supporting the 3 individual tables, and they do not go together,
    //they go in seperate activities altogether. No point in adding anything else
    static {
        //standard one
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieBestEntry.TABLE_NAME, ALL_MOVIE_BEST);
        //checking individual rows (for detail view)
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieBestEntry.TABLE_NAME + "/#", ONE_MOVIE_BEST);

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MoviePopularEntry.TABLE_NAME, ALL_MOVIE_POPULAR);

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MoviePopularEntry.TABLE_NAME + "/#", ONE_MOVIE_POPULAR);

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.FavoritesEntry.TABLE_NAME, ALL_FAVORITES);
        //individual rows: details view for favorites
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.FavoritesEntry.TABLE_NAME + "/#", ONE_FAVORITE);
    }

    /**
     * @param uri           uri of table to query
     * @param projection    tables to return with the cursor
     * @param selection     where _ = ?
     * @param selectionArgs ? = …
     * @param sortOrder     what to sort by
     * @return Cursor full of the information from the DB
     */
    @Override
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        String tableName;
        if( selection != null) {
            selection = selection + " = ?";
        } else{
            selection = ""; //Done so that we can select for ID in searching for a specific rowVector
        }


        //need to implement the uri matching into SQL statments or queries

        switch (sUriMatcher.match(uri)) {
            case ALL_MOVIE_BEST:
                //standard table for movies (regular Main activity)
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                tableName = MovieContract.MovieBestEntry.TABLE_NAME;
                break;
            case ONE_MOVIE_BEST:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                selection = selection + "_ID = " + uri.getLastPathSegment();
                tableName = MovieContract.MovieBestEntry.TABLE_NAME;
                break;
            case ALL_MOVIE_POPULAR:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                tableName = MovieContract.MoviePopularEntry.TABLE_NAME;
                break;
            case ONE_MOVIE_POPULAR:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                selection = selection + "_ID = " + uri.getLastPathSegment();
                tableName = MovieContract.MoviePopularEntry.TABLE_NAME;
                break;
            case ALL_FAVORITES:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                tableName = MovieContract.FavoritesEntry.TABLE_NAME;
                break;
            case ONE_FAVORITE:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                selection = selection + "_ID = " + uri.getLastPathSegment();
                tableName = MovieContract.FavoritesEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        //if ()
        Cursor c = movieDbHelper.getReadableDatabase()
                .query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), MovieContract.BASE_CONTENT_URI);
        return c;
    }

    /**
     * @param uri           uri for updating
     * @param contentValues values to insert
     * @return Uri that contains the location of the inserted row
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        long _id = 0;
        switch (sUriMatcher.match(uri)) {
            case ALL_MOVIE_BEST:
            case ONE_MOVIE_BEST:// one movie best included for robustness,
                //although  to use it would be very stupid
                //standard table for movies (regular Main activity)
                _id = movieDbHelper.getWritableDatabase().insert(MovieContract.MovieBestEntry.TABLE_NAME,
                        null, contentValues);
                return MovieContract.MovieBestEntry.buildBestUri(_id);
            case ALL_MOVIE_POPULAR:
            case ONE_MOVIE_POPULAR:
                _id = movieDbHelper.getWritableDatabase().insert(MovieContract.MoviePopularEntry.TABLE_NAME,
                        null, contentValues);
                return MovieContract.MoviePopularEntry.buildPopularUri(_id);
            case ALL_FAVORITES:
            case ONE_FAVORITE:
                _id = movieDbHelper.getWritableDatabase().insert(MovieContract.FavoritesEntry.TABLE_NAME,
                        null, contentValues);
                return MovieContract.FavoritesEntry.buildFavoriteUri(_id);
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * function that updates rows in database
     *
     * @param uri           uri of selection (table)
     * @param contentValues values to update with
     * @param selection     where = ?
     * @param selectionArgs ? = …
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case ALL_MOVIE_BEST:
            case ONE_MOVIE_BEST:// one movie best included for robustness,
                //although  to use it would be very stupid
                //standard table for movies (regular Main activity)
                return movieDbHelper.getWritableDatabase().update(MovieContract.MovieBestEntry.TABLE_NAME,
                        contentValues, selection, selectionArgs);
            case ALL_MOVIE_POPULAR:
            case ONE_MOVIE_POPULAR:
                return movieDbHelper.getWritableDatabase().update(MovieContract.MoviePopularEntry.TABLE_NAME,
                        contentValues, selection, selectionArgs);
            case ALL_FAVORITES:
            case ONE_FAVORITE:
                return movieDbHelper.getWritableDatabase().update(MovieContract.FavoritesEntry.TABLE_NAME,
                        contentValues, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException();
        }

    }

    /**
     * function that deletes rows from database
     *
     * @return number of rows deleted
     */
    @Override
    public int delete(@NonNull  Uri uri, String selection, String[] selectionArgs) {
        if( selection != null) {
            selection = selection + " = ?";
        }

        switch (sUriMatcher.match(uri)) {
            case ALL_MOVIE_BEST:
            case ONE_MOVIE_BEST:// one movie best included for robustness,
                //although  to use it would be very stupid to use this uri
                //standard table for movies (regular Main activity)
                return movieDbHelper.getWritableDatabase().delete(MovieContract.MovieBestEntry.TABLE_NAME,
                        selection, selectionArgs);
            case ALL_MOVIE_POPULAR:
            case ONE_MOVIE_POPULAR:
                return movieDbHelper.getWritableDatabase().delete(MovieContract.MoviePopularEntry.TABLE_NAME,
                        selection, selectionArgs);
            case ALL_FAVORITES:
            case ONE_FAVORITE:
                return movieDbHelper.getWritableDatabase().delete(MovieContract.FavoritesEntry.TABLE_NAME,
                        selection, selectionArgs);
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * @param uri to extract data from
     * @return String type that the URI refers to
     */
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ALL_MOVIE_POPULAR:
                return MovieContract.MoviePopularEntry.CONTENT_TYPE;
            case ALL_FAVORITES:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            case ALL_MOVIE_BEST:
                return MovieContract.MovieBestEntry.CONTENT_TYPE;
            case ONE_FAVORITE:
                return MovieContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            case ONE_MOVIE_POPULAR:
                return MovieContract.MoviePopularEntry.CONTENT_ITEM_TYPE;
            case ONE_MOVIE_BEST:
                return MovieContract.MovieBestEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Init the movieDBhelper, abstraction unit from the database itself.
     *
     * @return successFlag
     */
    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }
}
