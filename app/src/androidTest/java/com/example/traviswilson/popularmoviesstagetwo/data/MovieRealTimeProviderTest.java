package com.example.traviswilson.popularmoviesstagetwo.data;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by traviswilson on 7/10/17.
 */

@SmallTest
@RunWith(AndroidJUnit4.class)
public class MovieRealTimeProviderTest {
    Context mContext;
    public static final String LOG_TAG = "Provider Test";
    @Before
    public void setup(){
        mContext = InstrumentationRegistry.getTargetContext();
    }
//    @Test
//    public void testDatabase(){
//        Cursor c = mContext.getContentResolver()
//                .query(MovieContract.MovieBestEntry.CONTENT_URI, null , null, null, null );
//        if (c != null){
//        Log.v(LOG_TAG, "There are "+c.getCount()+" Movies in the Best Table");
//        }
//        Cursor c2 = mContext.getContentResolver()
//                .query(MovieContract.MoviePopularEntry.CONTENT_URI, null , null, null, null );
//        if (c2 != null){
//            Log.v(LOG_TAG, "There are "+c2.getCount()+" Movies in the Popular Table");
//        }
//        //Test to see if movies are the same
//        try {
//            c.moveToFirst();
//            c2.moveToFirst();
//            do {
//                assertFalse(c.getString(5).equals(c2.getString(5)));
//                Log.v(LOG_TAG, "Best Title: "+c.getString(5));
//                Log.v(LOG_TAG, "Popular Title "+c2.getString(5));
//            } while (c.moveToNext() && c2.moveToNext());
//            c.close();
//            c2.close();
//        } catch(NullPointerException e){
//            e.printStackTrace();
//        }
//
//    }
    @Test
    public void testFavorites(){
        Cursor c = mContext.getContentResolver()
                .query(MovieContract.FavoritesEntry.CONTENT_URI, null , null, null, null );
        if (c != null) Log.v(LOG_TAG, "There are "+c.getCount()+" Movies in Favorites DB");
        if (c == null) Log.v(LOG_TAG, "There are no Movies in Favorites DB");
    }
}
