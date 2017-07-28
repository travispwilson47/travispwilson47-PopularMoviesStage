package com.example.traviswilson.popularmoviesstagetwo.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.traviswilson.popularmoviesstagetwo.BuildConfig;
import com.example.traviswilson.popularmoviesstagetwo.R;
import com.example.traviswilson.popularmoviesstagetwo.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.util.Log.v;


/**
 * Created by traviswilson on 12/13/16. This class handles downloading and syncing information from
 * the movie DB, images themselves are NOT stored, the file URLs are. I implemented Picasso
 * for this app so it does automatic caching negating the need for File IO.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    ContentResolver contentResolver = null;
    final int NUM_DISPLAY_TYPES = 2;
    private static final String LOG_TAG = "Error in Sync Adapter";

    String[] JSONFields = {"poster_path", // just the desired information taken from the server
            "adult",
            "release_date",
            "title",
            "overview",
            "vote_average",
            "id"};
    private int NUM_MOVIES = 20;

    //Sync time in seconds . Sync time is every 1 hour
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEX_TIME = SYNC_INTERVAL / 3;

    final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p/";
    final String SIZE = "w185/";


    public MovieSyncAdapter(Context context, Boolean autoinit) {
        super(context, autoinit);
        contentResolver = getContext().getContentResolver();
    }

    /**
     * In this method we have to sync two things so the code may seem redundant
     *
     * @param account
     * @param extras
     * @param authority
     * @param provider
     * @param syncResult
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        for (int i = 0; i < NUM_DISPLAY_TYPES; i++) { // Two stages first Popular then Top_rated
            String jsonString = null;
            Log.v(LOG_TAG, "Begining Sync");

            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri uri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendPath(i == 0 ? "popular" : "top_rated")
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
            try {
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //Sad face
                    Log.e("Error!", "Null input stream");
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                jsonString = buffer.toString();
            } catch (Exception e) {
                Log.e("Error! JSON?>?", e.toString());
                return;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Error!", "Error closing stream", e);
                    }
                }
            }

            try {
                getInfoFromJSon(jsonString, i);
                contentResolver.notifyChange(MovieContract.BASE_CONTENT_URI,null,false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getInfoFromJSon(String jsonString, int stage) throws JSONException {
        final int POSTER_PATH_INDEX = 0;
        final int ADULT_INDEX = 1;
        final int RELEASE_DATE_INDEX = 2;
        final int TITLE_INDEX = 3;
        final int OVERVIEW_INDEX = 4;
        final int VOTE_AVERAGE_INDEX = 5;
        final int MOVIE_ID_INDEX = 6;

        if (jsonString == null) {
            return;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray results = jsonObject
                        .getJSONArray("results");

                Uri tableUriPopular = MovieContract.MoviePopularEntry.CONTENT_URI;
                Uri tableUriBest = MovieContract.MovieBestEntry.CONTENT_URI;
                switch (stage) {
                    case 0: getContext().getContentResolver().delete(tableUriPopular, null, null);
                    case 1: getContext().getContentResolver().delete(tableUriBest, null, null);
                }
                for (int i = 0; i < results.length(); i++) {
                    JSONObject currData = results.getJSONObject(i);
                    String posterPath = currData.getString(JSONFields[POSTER_PATH_INDEX]);
                    String urlImage = BASE_URL_IMAGE + SIZE + posterPath;
                    String adult = currData.getBoolean(JSONFields[ADULT_INDEX]) + "";
                    String releaseDate = currData.getString(JSONFields[RELEASE_DATE_INDEX]);
                    String title = currData.getString(JSONFields[TITLE_INDEX]);
                    String overview = currData.getString(JSONFields[OVERVIEW_INDEX]);
                    String voteAverage = currData.getString(JSONFields[VOTE_AVERAGE_INDEX]);
                    int movieId = currData.getInt(JSONFields[MOVIE_ID_INDEX]);

                    ContentValues movieVals = new ContentValues();


                    //Note that we never update favorites, that is done manually.

                    switch (stage) {
                        case 0:
                            movieVals.put(MovieContract.MoviePopularEntry.IMAGE_FILE, urlImage);
                            movieVals.put(MovieContract.MoviePopularEntry.ADULT_STATUS, adult);
                            movieVals.put(MovieContract.MoviePopularEntry.RELEASE_DATE, releaseDate);
                            movieVals.put(MovieContract.MoviePopularEntry.TITLE, title);
                            movieVals.put(MovieContract.MoviePopularEntry.SUMMARY, overview);
                            movieVals.put(MovieContract.MoviePopularEntry.RATING, voteAverage);
                            movieVals.put(MovieContract.MoviePopularEntry.MOVIE_ID, movieId);


                            //Now insert the data.
                            getContext().getContentResolver().insert(tableUriPopular, movieVals);
                            break;
                        case 1:
                            movieVals.put(MovieContract.MovieBestEntry.IMAGE_FILE, urlImage);
                            movieVals.put(MovieContract.MovieBestEntry.ADULT_STATUS, adult);
                            movieVals.put(MovieContract.MovieBestEntry.RELEASE_DATE, releaseDate);
                            movieVals.put(MovieContract.MovieBestEntry.TITLE, title);
                            movieVals.put(MovieContract.MovieBestEntry.SUMMARY, overview);
                            movieVals.put(MovieContract.MovieBestEntry.RATING, voteAverage);
                            movieVals.put(MovieContract.MovieBestEntry.MOVIE_ID, movieId);
                            getContext().getContentResolver().insert(tableUriBest, movieVals);
                            break;
                        default:
                            throw new UnsupportedOperationException();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Methods for the periodic syncing and setting up syncAdapter.

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        //Create the account type and default account

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));


        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        v("Made it to here", "Important 2");
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                v("Error in adding account", "");
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to set the syncing with the account once created
     * @param newAccount account to use to set periodic sync
     * @param context
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        //when we create the account, configure the syncadapter period
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEX_TIME);
        //Then we set the cr to sync automatically to enable the above code
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_provider_authority), true);
        //This is a method called on the first time account has been created so we sync immediately
        syncImmediately(context);

    }

    /**
     * Method to fire the sync adapter now. Fires the very first time the account has been created
     * so we turn the shared preferences on now.
     * @param context
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_provider_authority), bundle);
    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_provider_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
