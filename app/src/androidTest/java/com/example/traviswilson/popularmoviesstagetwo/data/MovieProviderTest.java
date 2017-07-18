package com.example.traviswilson.popularmoviesstagetwo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Created by traviswilson on 12/13/16.
 * Series of tests for the content provider. Test insert not included because already tested
 * in the testQuerry and testdeletes
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MovieProviderTest {
    private String LOG_TAG = "Test";
    private Context mContext;
    @Before
    public void setup(){
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testQuery() throws Exception {

        mContext.getContentResolver().delete(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null);

        ContentValues testValues = TestUtilities.createMovieValues();

       // assertFalse(testValues == null);

        mContext.getContentResolver().insert(MovieContract.MovieBestEntry.CONTENT_URI,
                testValues);

        Cursor goodCur = mContext.getContentResolver().query(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null,
                null,
                null);


        assertTrue(goodCur.getCount() == 1); // The number of columns put into the db by TestUtilities*//*
    }


    @org.junit.Test
    public void testUpdate() throws Exception {
        ContentValues testValues = TestUtilities.createMovieValues();
        mContext.getContentResolver().delete(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null);
        mContext.getContentResolver().insert(MovieContract.MovieBestEntry.CONTENT_URI,
                testValues);
        Cursor goodCur = mContext.getContentResolver().query(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertTrue(goodCur.getCount() == 1);
        testValues = TestUtilities.createMovieValues2();
        String whereClasue = "_ID = ?";
        String[] selectionArgs = {"1"};
        mContext.getContentResolver().update(MovieContract.MovieBestEntry.CONTENT_URI,
                testValues, whereClasue, selectionArgs);
        assertTrue(goodCur.getCount() == 1);
        goodCur = mContext.getContentResolver().query(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        goodCur.moveToFirst();
        assertTrue(goodCur.getString(1).equals("nay"));
    }

    @org.junit.Test
    public void testDelete() throws Exception {
        mContext.getContentResolver().delete(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null);
        ContentValues testValues = TestUtilities.createMovieValues2();
        mContext.getContentResolver().insert(MovieContract.MovieBestEntry.CONTENT_URI,
                testValues);
        Cursor goodCur = mContext.getContentResolver().query(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertTrue(goodCur.getCount() == 1);
        goodCur.moveToFirst();
        assertTrue(goodCur.getString(1).equals("nay"));
        mContext.getContentResolver().delete(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null);
        goodCur = mContext.getContentResolver().query(MovieContract.MovieBestEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertTrue(goodCur.getCount() == 0);
    }
}