package com.example.traviswilson.popularmoviesstagetwo.activities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.traviswilson.popularmoviesstagetwo.R;
import com.example.traviswilson.popularmoviesstagetwo.Utility;
import com.example.traviswilson.popularmoviesstagetwo.adapters.MovieAdapter;
import com.example.traviswilson.popularmoviesstagetwo.data.MovieContract;

/**
 * Encapsulates grid view in order to display the images.
 */
@TargetApi(14)
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String INTENT_KEY = "mainfragkey";
    private static final String SELECTED_KEY = "selected_key";
    private static final String LOG_TAG = "Error in MainFragment";

    private static final int MOVIE_LOADER = 0;

    private MovieAdapter movieAdapter; // Adapter used for displaying the posters
    private GridView mGridView;

    private int mPosition = GridView.INVALID_POSITION; // Initialize it to a non-Position

    //Specify columns we are using for the Projection
    private static final String[] gridViewColFavorites = {
        MovieContract.FavoritesEntry._ID,
        MovieContract.FavoritesEntry.IMAGE_FILE,
        MovieContract.FavoritesEntry.TITLE //Used for matching which item is selected
    };
    private static final String[] gridViewColBest = {
        MovieContract.MovieBestEntry._ID,
        MovieContract.MovieBestEntry.IMAGE_FILE,
        MovieContract.MovieBestEntry.TITLE
    };
    private static final String[] gridViewColPopular = {
        MovieContract.MoviePopularEntry._ID,
        MovieContract.MoviePopularEntry.IMAGE_FILE,
        MovieContract.MoviePopularEntry.TITLE
    };
    //Now the respective positions are saved here, note that these values are tied to the projections
    //defined above, so if those change these must change

    public static final int COL_ID = 0;
    public static final int COL_IMAGE_FILE = 1;
    public static final int COL_TITLE = 2;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This allows us to tell activities when an item has been selected,
     * they can then use this content uri to change the detail view to the information
     * selected.
     */
    interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }
    public void noMovies(){
        Toast toast = Toast.makeText(getActivity(), "No Movies", Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * This method works to attach the MovieAdapter (is-a CursorAdapter) to the gridview,
     * so as to inflate the View. This is the only view in this fragment, as the gridview
     * consists of the entire fragment.
     *
     * Note further that we input a blank cursor for the view, because we implement Loaders
     * for the app. This means in a following method we, create a loader, work with the
     * loaderManager, and swap out the cursor in the movieAdapter so that it then can use the fresh
     * data using this method.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MovieAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_main);
        mGridView.setAdapter(movieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    //This method takes the callback component of the activity
                    //then uses the callback method to send a URI back so
                    //it can be used to get the right column for the detail activity
                    // Anything containing this fragment must implement the onItemSelected method
                    //So as to handel the URI.

                    int row_ID = cursor.getInt(COL_ID);

                    //Make sure to swap out selected item as well in adapter
                    String title = cursor.getString(COL_TITLE);
                    onChangeSelected(title);

                    ((Callback) getActivity()).onItemSelected(Utility.getTableVectorUri(
                            getActivity(),
                            row_ID
                    ));

                }
                mPosition = position;
            }
        });

        //If there is a saved instance state then get the information out of it here.

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {

            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // This code does exactly that: if there is a position selected, then we save it.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        mPosition = mGridView.getFirstVisiblePosition();
        outState.putInt(SELECTED_KEY, mPosition);

        super.onSaveInstanceState(outState);
    }

    /**
     * Method is overridden in order to get the loader set up for the activity.
     * It will actually be created in onCreateLoader, but the process is started and the ID
     * is set here; we pass in this because that hooks up the loader to this fragment.
     * @param savedInstanceState
     */


    /**
     * Method that is fired when the MainActivity detects a change in the settings,
     * so a different type wants to be displayed. The loader must change as a result, and this is
     * done through onCreateLoader
     * @return successful change of things
     */
    public boolean onPreferredDisplayTypeChange(){
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        return true;
    }

    /**
     * Method that is fired when the User taps a grid tiem
     */
    public void onChangeSelected(String newTitle){
        movieAdapter.changeSelected(newTitle);
    }

    /**
     * This is called when a new loader needs to be made; only one is used in this fragment
     * Here we create the loader and return it.
     *
     * Loaders are implemented so as Load data from our content provider on the cursor. The loader
     * takes in the data from the querry we implicitly do with the constuctor, and then in
     * onLoadFinished a new cursor is popped out for swaping out with the MovieAdapter
     * (is-a cursor Adapter)
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i , Bundle bundle){
        //Our sort order is whatever is put in from the data source
        Log.v(LOG_TAG, "Beginning Load");
        switch(Utility.getPreferredDisplayType(getActivity())){
            case "favorite":
                return new CursorLoader(getActivity(),
                        Utility.getTableUri(getActivity()),
                        gridViewColFavorites,
                        null,
                        null,
                        null
                        );


            case "top rated":
                return new CursorLoader(getActivity(),
                        Utility.getTableUri(getActivity()),
                        gridViewColBest,
                        null,
                        null,
                        null
                );

            case "most popular":
                return new CursorLoader(getActivity(),
                        Utility.getTableUri(getActivity()),
                        gridViewColPopular,
                        null,
                        null,
                        null
                );
            default:
                throw new UnsupportedOperationException();
        }

    }

    /**
     * Fairly simple method, just takes the cursor gotten from the Loader when it is done loading
     * and sticks it in the adapter.
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "onLoadFinishedCalled");
        MainActivity context = (MainActivity) getActivity();
        if(data.getCount() == 0){
            noMovies();
            movieAdapter.swapCursor(null);
            return;
        }
        data.moveToFirst();
        if (context.isTwoPane()) { // Make sure to give it the first, default
            Intent intent = new Intent(MainActivity.INTENT_FILTER);
            intent.putExtra(INTENT_KEY, Utility.getTableVectorUri(context, data.getInt(COL_ID)).toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        Log.v(LOG_TAG, "Gridview Position" + mPosition);
        movieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            //To avoid any abrupt move around on turning the device or updates
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    /**
     * Just clean out the adapter, take the old, useless data out
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { movieAdapter.swapCursor(null);}

}

