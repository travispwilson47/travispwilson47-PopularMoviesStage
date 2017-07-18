package com.example.traviswilson.popularmoviesstagetwo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.traviswilson.popularmoviesstagetwo.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by traviswilson on 6/10/17.
 * Activity used only by phones. Just instantates the fragment
 */

public class ReviewActivity extends AppCompatActivity {
    private static final String reviewFragmentTag = "revTag";
    private static final String LOG_TAG = "Review Activity";

    @Override
    public void onCreate(Bundle savedInstanceState ){
        Bundle bundle = new Bundle();
        setContentView(R.layout.activity_review);
        Log.v(LOG_TAG, getIntent().getStringExtra(DetailFragment.REVIEW_URL_STRING_TAG));
        bundle.putString(DetailFragment.REVIEW_URL_STRING_TAG, getIntent().getStringExtra(DetailFragment.REVIEW_URL_STRING_TAG));
        ReviewFragment frag = new ReviewFragment();
        frag.setArguments(bundle);
        if (savedInstanceState != null)
        getFragmentManager().beginTransaction().replace(R.id.review_activity_container, frag, reviewFragmentTag).commit();
        super.onCreate(savedInstanceState);
    }
}
