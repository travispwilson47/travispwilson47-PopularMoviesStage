package com.example.traviswilson.popularmoviesstagetwo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.traviswilson.popularmoviesstagetwo.activities.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by traviswilson on 7/15/17.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestUtilities {
    Context mContext;
    @Before
    public void setUp(){
        mContext = InstrumentationRegistry.getTargetContext();
    }
    @Test
    public void testNetwork(){
        Log.v("Test", ""+MainActivity.isNetworkAvailable(mContext));
    }
}
