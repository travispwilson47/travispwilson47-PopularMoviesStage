package com.example.traviswilson.popularmoviesstagetwo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.traviswilson.popularmoviesstagetwo.AddFavoriteDialog;
import com.example.traviswilson.popularmoviesstagetwo.R;

/**
 * Created by traviswilson on 12/25/16.
 * Note that this fires only when we are in phone mode.
 */
public class DetailActivity extends AppCompatActivity implements AddFavoriteDialog.Callback{

    public static final String DETAIL_FRAGMENT_TAG = "detTag";
    private static final String LOG_TAG = "Error in DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null)
        getFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container_act,
                        new DetailFragment(),DETAIL_FRAGMENT_TAG).commit();

    }

    /**
     * This method simply relays communication from the AddFavoriteDialog to the DetailFragment that
     * should handle the event. According to the android documentation, all communication between Fragments
     * should occur via the parent activity. This is a manifestation of that pattern.
     * @param isFavorite to simply be passed on
     */
    @Override
    public void onAffButtonClicked(boolean isFavorite) {
        AddFavoriteDialog.Callback listener;
        try {
            listener =
                    (AddFavoriteDialog.Callback) getFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
        } catch (ClassCastException e){
            Log.e(LOG_TAG, "Could not pass down AddFavoriteDialog");
            return;
        }
        listener.onAffButtonClicked(isFavorite);

    }
}
