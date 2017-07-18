package com.example.traviswilson.popularmoviesstagetwo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.traviswilson.popularmoviesstagetwo.AddFavoriteDialog;
import com.example.traviswilson.popularmoviesstagetwo.R;
import com.example.traviswilson.popularmoviesstagetwo.Utility;
import com.example.traviswilson.popularmoviesstagetwo.settings.SettingsActivity;
import com.example.traviswilson.popularmoviesstagetwo.sync.MovieSyncAdapter;

import static com.example.traviswilson.popularmoviesstagetwo.activities.DetailActivity.DETAIL_FRAGMENT_TAG;

/**
 * Created by traviswilson on 12/13/16. Main activity for displaying. On phone mode it just has
 * the array of movies. On tablet it has the detail fragment as well.
 */
public class MainActivity extends AppCompatActivity implements MainFragment.Callback , DetailFragment.Callback,
AddFavoriteDialog.Callback{

    public static final String SWITCH_FRAGMENT_NAME = "switchfrag";
    public static final String MAIN_ACTIVITY_TAG  = "detailTag";
    public static final String REVIEW_FRAGMENT_TAG = "reviewTag";
    public static final String INTENT_FILTER = "mainActIntentFilter";
    public static final String DETAIL_URI_INTENT_KEY = "detkey";
    private static final String LOG_TAG = "Error in MainActivity";

    boolean detailFragmentAddedFlag = true;

    private boolean twoPane; // For tablet UI

    private String displaySetting;
    private String rowUri; //Used in the broadcast receiver - this is the first URI.

    private BroadcastReceiver mainReceiver;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    /**
     * Here the menu is inflated. Just an option to go to the settings
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            detailFragmentAddedFlag = false;
        if (!isNetworkAvailable(getApplicationContext())
                && !Utility.getPreferredDisplayType(getApplicationContext()).equals("favorite")){
            Toast.makeText(getApplicationContext(), "No internet, application will not sync", Toast.LENGTH_SHORT).show();
        }
        displaySetting = Utility.getPreferredDisplayType(this);

        setContentView(R.layout.activity_main);
        //If there is a detail container (only defined in the activity_main layout of
        // screen sizes of sw600dp) then we know that it must be a tablet and we are two pane mode.

        twoPane = findViewById(R.id.detail_fragment_container) != null;

        if (!twoPane){
            //Here we register a local Broadcast receiver in order to listen to a change in the favorites
            //DB when someone adds or removes from favorites, and thus the Gridview in mainFragment has to be
            //updated.
            mainReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(Utility.getPreferredDisplayType(getApplicationContext()).equals("favorite"))
                        ((MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main))
                                .onPreferredDisplayTypeChange(); //Although the display type has not changed
                } //We call this method to reload what favorites are in the grid, as one has been removed.
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(mainReceiver, new IntentFilter(INTENT_FILTER));
        } else {
            //Here we use the same reference to do something different: notice when DetailFragment is ready
            //to be added because the mainFragment has loaded the movies.
            mainReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String newUri = intent.getStringExtra(MainFragment.INTENT_KEY);
                    if (!newUri.equals(rowUri)) { //Ok so the sync actually did something different
                        //Ok so the sync actually did something different
                        //so we have to change what movies are selected (since the detail Fragment
                        //might otherwise be displaying a movie that is no longer displayed in the app
                        rowUri = newUri;
                        firstDataGenerated(newUri);
                    }
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(mainReceiver, new IntentFilter(INTENT_FILTER));
        }

        MovieSyncAdapter.initializeSyncAdapter(this);

    }

    public boolean isTwoPane(){
        return twoPane;
    }

    /**
     * We wait until here to add the fragment (if two pane mode) because this method is called when
     * the detail fragment has completed its loading, so there is something to actually populate
     * the fragment with
     */
    private void firstDataGenerated(String rowUri){
        if (detailFragmentAddedFlag) { // very first time
            Bundle bundle = new Bundle();
            bundle.putString(MAIN_ACTIVITY_TAG, rowUri);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
            detailFragmentAddedFlag = false;
        }
//        } else{ //already have the fragment, but new sync or display type change
//            ((DetailFragment) getFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG))
//                    .onDetailChange(rowUri);
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.settings_menu){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (!isNetworkAvailable(getApplicationContext())
                && !Utility.getPreferredDisplayType(getApplicationContext()).equals("favorite")){
            Toast.makeText(getApplicationContext(), "No internet, application will not sync", Toast.LENGTH_SHORT).show();
        }
        if (!Utility.getPreferredDisplayType(this).equals(displaySetting)){
            MainFragment mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
            mainFragment.onPreferredDisplayTypeChange(); //Contains the code to make sure the detail fragment
            displaySetting = Utility.getPreferredDisplayType(this);
            //gets the first uri
        }
    }

    /**
     * method fires when a gridItem in the MainFragment is selected. Note that within that method,
     * and not this one, a change in the selected visuals is handled, so it is not done here
     * @param contentUri uriTo send to the detailFragment
     */

    @Override
    public void onItemSelected(Uri contentUri){
        if(twoPane){ //Then there is fragment to send it to.
            getFragmentManager().popBackStackImmediate();
            DetailFragment detailFragment =
                    (DetailFragment) getFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            detailFragment.onDetailChange(contentUri);

        } else{
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DETAIL_URI_INTENT_KEY, contentUri.toString()); //note that it must be reparsed
            startActivity(intent);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mainReceiver);
    }


    /**
     * Callback Method fires when reviewPlay button Selected only in TwoPane mode, sends review Uri for new ReviewFragment
     * @param reviewsUri Uri to send to acquire API data
     */
    @Override
    public void onReadReviewsPushed(String reviewsUri) {
            DetailFragment detailFragment =  (DetailFragment) getFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

            ReviewFragment reviewFragment = new ReviewFragment();

            Bundle bundle = new Bundle();
            bundle.putString(DetailFragment.REVIEW_URL_STRING_TAG, reviewsUri);
            reviewFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().remove(detailFragment).add(R.id.detail_fragment_container, reviewFragment, REVIEW_FRAGMENT_TAG)
                    .addToBackStack(SWITCH_FRAGMENT_NAME).commit();
    }

    /**
     * Method only called if modeTwoPane and when displaying Favorites mode to change the Favorites Fragment
     * Display array
     */
    @Override
    public void favoriteChanged(boolean isFavorite) {
        if(isFavorite) {
            MainFragment fragment = (MainFragment)
                    getFragmentManager().findFragmentById(R.id.fragment_main);
            fragment.onPreferredDisplayTypeChange();
        }
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
            throw new ClassCastException("Fragment Must implement AddFavoriteDialog.Callback");
        }
        listener.onAffButtonClicked(isFavorite);
    }

}
