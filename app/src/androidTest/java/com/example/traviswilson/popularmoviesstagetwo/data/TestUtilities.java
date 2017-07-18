package com.example.traviswilson.popularmoviesstagetwo.data;

import android.content.ContentValues;

/**
 * Created by traviswilson on 12/13/16.
 */
public class TestUtilities {
    static ContentValues createMovieValues() {
        ContentValues movieVals = new ContentValues();
        movieVals.put(MovieContract.MovieBestEntry.IMAGE_FILE, "file/Image");
        movieVals.put(MovieContract.MovieBestEntry.ADULT_STATUS, "true");
        movieVals.put(MovieContract.MovieBestEntry.RATING, "Arrrr!");
        movieVals.put(MovieContract.MovieBestEntry.RELEASE_DATE, "Whenever");
        movieVals.put(MovieContract.MovieBestEntry.SUMMARY, "The world ends");
        movieVals.put(MovieContract.MovieBestEntry.TITLE, "2012");
        movieVals.put(MovieContract.MovieBestEntry.MOVIE_ID, "42");
        return movieVals;
    }
    static ContentValues createMovieValues2(){
        ContentValues movieVals = new ContentValues();
        movieVals.put(MovieContract.MovieBestEntry.IMAGE_FILE, "nay");
        movieVals.put(MovieContract.MovieBestEntry.ADULT_STATUS, "nay");
        movieVals.put(MovieContract.MovieBestEntry.RATING, "nay");
        movieVals.put(MovieContract.MovieBestEntry.RELEASE_DATE, "nay");
        movieVals.put(MovieContract.MovieBestEntry.SUMMARY, "nay");
        movieVals.put(MovieContract.MovieBestEntry.TITLE, "nay");
        movieVals.put(MovieContract.MovieBestEntry.MOVIE_ID, "42");
        return movieVals;
    }
}
