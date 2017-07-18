package com.example.traviswilson.popularmoviesstagetwo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by traviswilson on 12/10/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movie.db";

    private static final int DatabaseVersion = 1;

    MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_BEST_TABLE = "CREATE TABLE " + MovieContract.MovieBestEntry.TABLE_NAME
                + " (" +
                MovieContract.MovieBestEntry._ID + " INTEGER PRIMARY KEY," + // this is inherited quality
                MovieContract.MovieBestEntry.ADULT_STATUS + " TEXT NOT NULL, " + //so it isn't in the contract, but the others are
                MovieContract.MovieBestEntry.IMAGE_FILE + " TEXT NOT NULL, " +
                MovieContract.MovieBestEntry.RATING + " TEXT NOT NULL, " +
                MovieContract.MovieBestEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieBestEntry.TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieBestEntry.SUMMARY + " TEXT NOT NULL, " +
                MovieContract.MovieBestEntry.MOVIE_ID + " INTEGER NOT NULL, "
                +" UNIQUE (" + MovieContract.FavoritesEntry.TITLE + ") ON CONFLICT REPLACE);";
        final String SQL_CREATE_MOVIE_POPULAR_TABLE = "CREATE TABLE " + MovieContract.MoviePopularEntry.TABLE_NAME
                + " (" +
                MovieContract.MoviePopularEntry._ID + " INTEGER PRIMARY KEY," + // this is inherited quality
                MovieContract.MoviePopularEntry.ADULT_STATUS + " TEXT NOT NULL, " + //so it isn't in the contract, but the others are
                MovieContract.MoviePopularEntry.IMAGE_FILE + " TEXT NOT NULL, " +
                MovieContract.MoviePopularEntry.RATING + " TEXT NOT NULL, " +
                MovieContract.MoviePopularEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MoviePopularEntry.TITLE + " TEXT NOT NULL, " +
                MovieContract.MoviePopularEntry.SUMMARY + " TEXT NOT NULL, " +
                MovieContract.MoviePopularEntry.MOVIE_ID + " INTEGER NOT NULL, "
                +" UNIQUE (" + MovieContract.FavoritesEntry.TITLE + ") ON CONFLICT REPLACE);";
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.FavoritesEntry.TABLE_NAME
                + " (" +
                MovieContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.FavoritesEntry.ADULT_STATUS + " TEXT NOT NULL, " +
                MovieContract.FavoritesEntry.IMAGE_FILE + " TEXT NOT NULL, " +
                MovieContract.FavoritesEntry.RATING + " TEXT NOT NULL, " +
                MovieContract.FavoritesEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoritesEntry.TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoritesEntry.SUMMARY + " TEXT NOT NULL, " +
                MovieContract.FavoritesEntry.MOVIE_ID + " INTEGER NOT NULL, "
                +" UNIQUE (" + MovieContract.FavoritesEntry.TITLE + ") ON CONFLICT REPLACE);";
        // We set up a on Uniqueness requirement here because there should only be data for one movie,
        // no space wasting repeats
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_BEST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_POPULAR_TABLE);
        //No foreign key strategy needed here as the design includes 2 separate tables, they may or may not have overlapping data
    }
    //basically never fires in real time, only if Database version number is changed (is final).
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ MovieContract.FavoritesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ MovieContract.MoviePopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ MovieContract.MovieBestEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

