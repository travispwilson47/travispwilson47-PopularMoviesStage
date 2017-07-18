package com.example.traviswilson.popularmoviesstagetwo.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traviswilson.popularmoviesstagetwo.AddFavoriteDialog;
import com.example.traviswilson.popularmoviesstagetwo.BuildConfig;
import com.example.traviswilson.popularmoviesstagetwo.R;
import com.example.traviswilson.popularmoviesstagetwo.Trailer;
import com.example.traviswilson.popularmoviesstagetwo.Utility;
import com.example.traviswilson.popularmoviesstagetwo.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Fragment containing the other details about the movie selected. The intent may or may not
 * contain the URI, and there may or may not be URI's being sent to replace the data
 *
 * Note that this fragment as well implements loaders.
 * Created by traviswilson on 12/25/16.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AddFavoriteDialog.Callback {

    public static final String REVIEW_URL_STRING_TAG = "urlTag2";
    public static final String DIALOG_ARGUMENT_KEY = "stgKey";
    private static final String LOG_TAG = "Detail Fragment";

    public static final int SELECTION_ALPHA = 44;

    boolean isFavorite;
    boolean clickSet = false; //Boolean to see if the clickListener on the Fav. button has been set,
    //useful due to the method call flow in this Fragment when it is altered by MainActivity events and the like
    boolean isTwoPane;
    //place it in the onCreate method since it requires a title to check

    TextView summaryView;
    RatingBar ratingView;
    TextView titleView;
    TextView releaseDateView;
    ImageView favoriteButton;
    ImageView playTrailer;
    ImageView readReviews;

    String title;
    Uri mContentUri;
    Cursor infoCursor; //Kept for use of Favorites insert

    int movieID;  //Used for when either trailers or reviews are clicked on




    /*
        Projections for the detail activity
     */
    private static final String[] projectionFavorites = {
            MovieContract.FavoritesEntry.IMAGE_FILE, // This is taken for the Favorites insert
            MovieContract.FavoritesEntry.TITLE,
            MovieContract.FavoritesEntry.SUMMARY,
            MovieContract.FavoritesEntry.RELEASE_DATE,
            MovieContract.FavoritesEntry.RATING,
            MovieContract.FavoritesEntry.ADULT_STATUS,
            MovieContract.FavoritesEntry.MOVIE_ID
    };
    private static final String[] projectionBest = {
            MovieContract.MovieBestEntry.IMAGE_FILE,
            MovieContract.MovieBestEntry.TITLE,
            MovieContract.MovieBestEntry.SUMMARY,
            MovieContract.MovieBestEntry.RELEASE_DATE,
            MovieContract.MovieBestEntry.RATING,
            MovieContract.MovieBestEntry.ADULT_STATUS,
            MovieContract.MovieBestEntry.MOVIE_ID
    };
    private static final String[] projectionPopular = {
            MovieContract.MoviePopularEntry.IMAGE_FILE,
            MovieContract.MoviePopularEntry.TITLE,
            MovieContract.MoviePopularEntry.SUMMARY,
            MovieContract.MoviePopularEntry.RELEASE_DATE,
            MovieContract.MoviePopularEntry.RATING,
            MovieContract.MoviePopularEntry.ADULT_STATUS,
            MovieContract.MoviePopularEntry.MOVIE_ID
    };
    /*
     Constants tied to the above
     */

    public static final int COL_IMAGE_FILE = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_SUMMARY = 2;
    public static final int COL_RELEASE_DATE = 3;
    public static final int COL_RATING = 4;
    public static final int COL_ADULT_STATUS = 5;
    public static final int COL_MOVIE_ID = 6;

    public static final int MOVIE_LOADER = 0;
    public static final int FAVORITE_CHECK_LOADER = 1;



    /**
     * Here we set either the contentdata of everything by calling the apropriate methods or
     * just have the blank detail layout (if tablet mode)
     *
     * Also we dynamically set a variety of layout items to the desired aspect ratios.
     * @param savedInstanceState present if device rotation
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        //We check to see if there is any bundles in the intent (may contain URI, if not in
        //Tablet mode
        super.onCreate(savedInstanceState);
        String uri;
        uri = getActivity().getIntent().getStringExtra(MainActivity.DETAIL_URI_INTENT_KEY);

        if ( uri == null){ //tablet mode
            mContentUri = Uri.parse(getArguments().getString(MainActivity.MAIN_ACTIVITY_TAG));
            isTwoPane = true;
        } else {
            mContentUri = Uri.parse(uri);
        }
    }
    public static void changefavoriteButtonImage(boolean isFavorite, Context context){
        ImageView favoriteButton =
                (ImageView) ((Activity) context).findViewById(R.id.favorite_button);
        if (isFavorite){
            favoriteButton.setImageResource(R.drawable.fav_button_unpressed);
        } else {
            favoriteButton.setImageResource(R.drawable.fav_button_pressed);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        resetSelectors(getActivity());
    }

    interface Callback {
        void onReadReviewsPushed(String reviewsUri);
        void favoriteChanged(boolean isFavorite);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        summaryView = (TextView) v.findViewById(R.id.description_text);
        ratingView = (RatingBar) v.findViewById(R.id.ratingBar);
        titleView = (TextView) v.findViewById(R.id.title);
        releaseDateView = (TextView) v.findViewById(R.id.release_date);
        favoriteButton = (ImageView) v.findViewById(R.id.favorite_button);

        playTrailer = (ImageView) v.findViewById(R.id.selectionLeft);
        readReviews = (ImageView) v.findViewById(R.id.selectionRight);

        playTrailer.setImageAlpha(00);
        readReviews.setImageAlpha(00);

        return v;
    }
    @Override
    public void onStop(){
        super.onStop();
        clickSet = false; //To prevent clickListeners not being set again when the frag. is resumed
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        beginLoadData();
        super.onActivityCreated(savedInstanceState);
    }

    void beginLoadData(){
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    /**
     * This method is where the click listeners are set up for favorite button to actually add
     * to favorites, and for the play trailers and read reviews to do as expected. Had to wait until
     * here instead of onCreate because we needed a title for Favorite button and a movie ID for
     * the other buttons
     */
    private void setUpFavoriteAndButtons(){

        if (!clickSet){

            //Set up the favoriteButton
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView favoriteButtonAc = (ImageView) view;
                    //Short button animation
                    Bundle b = new Bundle();
                    b.putBoolean(DIALOG_ARGUMENT_KEY, isFavorite);
                    AddFavoriteDialog dialog = new AddFavoriteDialog();
                    dialog.setArguments(b);
                    dialog.show(getFragmentManager(), "favorites"); //Never need to retrieve dialog
                }
            });
            //Set up the other buttons
            //TODO: Substitute this
            if (MainActivity.isNetworkAvailable(getActivity())) {

                //the loading of the reviews the proper clicklistner will be set later when
                //the information is present.

                //For reviews we just launch the Fragment/Activity with the URL and wait until then to
                //actually load them so we don't have to load them if the user does not need them
                readReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        readReviews.setImageAlpha(SELECTION_ALPHA);
                        final String reviewUrl = getReviewUrlFromID(movieID);
                        if (isTwoPane){ //Use callback to get this Fragment Removed and ReviewActivity Added.
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.onReadReviewsPushed(reviewUrl);
                        } else{ //then we need to launch the ReviewActivity
                            Intent intent = new Intent(getActivity(), ReviewActivity.class);
                            intent.putExtra(DetailFragment.REVIEW_URL_STRING_TAG, reviewUrl); //note that it must be reparsed
                            startActivity(intent);
                        }
                    }
                });
                new GetTrailerTask().execute("http://api.themoviedb.org/3/movie/" + movieID  +
                        "/videos?api_key=" + BuildConfig.MOVIE_DB_API_KEY);



            } else {
                playTrailer.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Toast.makeText(getActivity(), "No Internet to Check Trailers", Toast.LENGTH_SHORT).show();
                    }
                });
                readReviews.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Toast.makeText(getActivity(), "No Internet to Check Reviews", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        clickSet = true;
    }
    private static String getReviewUrlFromID(int ID){
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(ID+"")
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY)
                .build();
        return uri.toString();
    }


    /**
     * Separate private method for setting up the Trailers because the Youtube Link has to be retrived
     * from the API
     */
    private void setUpTrailers(final Trailer[] trailers){
        if (trailers != null && trailers.length != 0) {
            playTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playTrailer.setImageAlpha(SELECTION_ALPHA);
                    Bundle b = new Bundle();
                    b.putParcelableArray(DIALOG_ARGUMENT_KEY, trailers);
                    PlayTrailersDialog dialog = new PlayTrailersDialog();
                    dialog.setArguments(b);
                    dialog.show(getFragmentManager(), "trailers"); //Access to the Dialog is never needed
                }
            });
        }

    }
    private static void resetSelectors(Context context){
        ((ImageView) ((Activity) context).findViewById(R.id.selectionLeft)).setImageAlpha(00);
        ((ImageView) ((Activity) context).findViewById(R.id.selectionRight)).setImageAlpha(00);
    }
    public static class ViewDescriptionDialog extends DialogFragment{
        String DescriptionText;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            DescriptionText = getArguments().getString(DIALOG_ARGUMENT_KEY);
            builder.setMessage(DescriptionText);
            return builder.create();
        }
    }
    public static class PlayTrailersDialog extends DialogFragment {
        final String DIALOG_TITLE = "Select Trailer";
        final String TRAILER_URL_STUB_INTERNET = "https://www.youtube.com/watch?v=" ;
        final String TRAILER_URL_STUB_APP = "vnd.youtube:";
        final String NO_TRAILERS = "No Trailers Available";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Trailer[] trailers = (Trailer[]) getArguments().getParcelableArray(DIALOG_ARGUMENT_KEY);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (trailers != null) {
                builder.setTitle(DIALOG_TITLE);

                builder.setItems(trailerToTrailerTitleArray(trailers), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String trailerUrlID = trailers[i].getYoutubeID();

                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TRAILER_URL_STUB_APP + trailerUrlID));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(TRAILER_URL_STUB_INTERNET + trailerUrlID));
                        try {
                            startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            startActivity(webIntent);
                        }
                        resetSelectors(getActivity());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Method automatically dismisses dialog
                        resetSelectors(getActivity());
                    }
                });
            } else{
                builder.setMessage(NO_TRAILERS);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Method automatically dismisses dialog
                        resetSelectors(getActivity());
                    }
                });
            }
            return builder.create();
        }
    }
    @Override
    public void onAffButtonClicked(boolean isFavorite) {
        if (isFavorite){
            new RemoveFavoriteTask().execute(title);
        } else{
            new AddFavoriteTask().execute();
        }
    }

    /**
     * Takes the cursor and extracts the data from it and puts it in the respective textViews
     *
     * NOTE: Although the cursor we are using is not in a strictly thread safe manor, we are not
     * mutating the cursor in any way, just extracting its data, so no need for synchronized statements
     * here or otherwise.
     * @param cursor
     */
    private void loadData(final Cursor cursor){
        final String BASE_ADULT_TEXT = "\n\nThis movie is ";

        infoCursor = cursor;
        infoCursor.moveToFirst();

        movieID = infoCursor.getInt(COL_MOVIE_ID);

        String summary = infoCursor.getString(COL_SUMMARY);
        summaryView.setText(infoCursor.getString(COL_SUMMARY));

        float primitiveRating = Float.parseFloat(infoCursor.getString(COL_RATING));
        float s1 = (int) (primitiveRating + 0.5);


        ratingView.setRating(s1/2);

       title = infoCursor.getString(COL_TITLE);

        //Some minor aesthetic readjustments

        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        if( ( rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && (Utility.getScreenWidth(getActivity()) < 600) ) {
            if (title.length() <= 6) {
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
            } else if (title.length() <= 27) {
                titleView.setMinLines(2);
            } else if (title.length() <= 50) {
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
                releaseDateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 39);
            } else {
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                releaseDateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 29);
            }
        }
        String adultText = Boolean.parseBoolean(infoCursor.getString(COL_ADULT_STATUS)) ?
                "meant for adult audiences." : "safe for kids.";
        summaryView.append(BASE_ADULT_TEXT+adultText);
        releaseDateView.setText(infoCursor.getString(COL_RELEASE_DATE));
        summaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ViewDescriptionDialog d =  new ViewDescriptionDialog();
                Bundle b = new Bundle();
                b.putString(DIALOG_ARGUMENT_KEY, summaryView.getText().toString());
                d.setArguments(b);
                d.show(getFragmentManager(), "Description Tag");
            }
        });
        titleView.setText(title);

        if (!Utility.getPreferredDisplayType(getActivity()).equals("favorite")) {
            Log.v(LOG_TAG, "Just before title");
            getLoaderManager().destroyLoader(FAVORITE_CHECK_LOADER);
            getLoaderManager().initLoader(FAVORITE_CHECK_LOADER, null, this);
        } else{
            isFavorite = true;
            changefavoriteButtonImage(false, getActivity());
            setUpFavoriteAndButtons();
        }
    }
    public void onDetailChange(Uri newUri){
        mContentUri = newUri;
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
    public void onFavoriteChanged(){
        if (isTwoPane){
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.favoriteChanged(Utility.getPreferredDisplayType(getActivity()).equals("favorite"));
        } else{ //we will use broadcast reciever in this case
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(MainActivity.INTENT_FILTER));
        }
    }

    /**
     * Container class for trailer information. Trailer information cannot be changed, just accessed.
     */
    private static String[] trailerToTrailerTitleArray(@NonNull Trailer[] trailers){
        String[] titles = new String[trailers.length];
        for (int i = 0 ; i< trailers.length; i++){
            titles[i] = trailers[i].getVideoTitle();
        }
        return titles;
    }

    //TODO: Look at lifecycle errors for these loaders
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
        //Our sort order is whatever is put in from the datFa
        Log.v(LOG_TAG, "Just Before title 2");
        if (i == MOVIE_LOADER) {
            switch (Utility.getPreferredDisplayType(getActivity())) {
                case "favorite":
                    return new CursorLoader(getActivity(),
                            mContentUri,
                            projectionFavorites,
                            null,
                            null,
                            null
                    );


                case "top rated":
                    return new CursorLoader(getActivity(),
                            mContentUri,
                            projectionBest,
                            null,
                            null,
                            null
                    );

                case "most popular":
                    return new CursorLoader(getActivity(),
                            mContentUri,
                            projectionPopular,
                            null,
                            null,
                            null
                    );
                default:
                    throw new UnsupportedOperationException();
            }
        } else { // i == FAVORITE_CHECK_LOADER . In this case we only want to check if it is null,

            String[] selectionArgs = {title};
            Log.v(LOG_TAG, "Movie Title" + title);

            switch (Utility.getPreferredDisplayType(getActivity())) {
                case "top rated": case "most popular":
                    return new CursorLoader(getActivity(), //so we don't need a projection
                            MovieContract.FavoritesEntry.CONTENT_URI,
                            projectionBest, //just recycling the same thing.
                            MovieContract.FavoritesEntry.TITLE, //the actual SQL statement implemented inside
                            selectionArgs,
                            null
                    );
                default:
                    throw new UnsupportedOperationException();
            }
        }


    }

    /**
     * Take new cursor data and put it in the respective views
     *
     * Or check to see if there were any matching movies in the Favorite database, if there
     * was, then this movie is a Favorite.
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if ( loader.getId() == MOVIE_LOADER) {
            loadData(data);
        } else{
            isFavorite = data != null && data.moveToFirst();
            Log.v(LOG_TAG, "This is favorite"+isFavorite);
            Log.v(LOG_TAG, "The cursor size"+data.getCount());
            setUpFavoriteAndButtons();
            changefavoriteButtonImage(!isFavorite, getActivity()); // Because this is used to change
            //The favorite button after it is added/removed, we pass in !isFavorite
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {  }

    private class AddFavoriteTask extends AsyncTask<Void, Void, Void>{

        private static final String message = "Added To Favorites";

        @Override
        protected Void doInBackground(Void... voids) {
            Log.v(LOG_TAG, "Adding to Favorties");
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.FavoritesEntry.IMAGE_FILE, infoCursor.getString(COL_IMAGE_FILE));
            movieValues.put(MovieContract.FavoritesEntry.TITLE, infoCursor.getString(COL_TITLE));
            movieValues.put(MovieContract.FavoritesEntry.SUMMARY,infoCursor.getString(COL_SUMMARY));
            movieValues.put(MovieContract.FavoritesEntry.RELEASE_DATE, infoCursor.getString(COL_RELEASE_DATE));
            movieValues.put(MovieContract.FavoritesEntry.RATING,infoCursor.getString(COL_RATING) );
            movieValues.put(MovieContract.FavoritesEntry.ADULT_STATUS, infoCursor.getString(COL_ADULT_STATUS));
            movieValues.put(MovieContract.FavoritesEntry.MOVIE_ID, infoCursor.getString(COL_MOVIE_ID));

            Uri favoriteTableUri = MovieContract.FavoritesEntry.CONTENT_URI;

            getContext().getContentResolver().insert(favoriteTableUri, movieValues);

            return null;
        }
        @Override
        protected void onPostExecute(Void voids) {
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            toast.show();
            isFavorite = true;
            onFavoriteChanged();
        }
    }


    private class RemoveFavoriteTask extends AsyncTask<String, Void, Void>{

        private static final String message = "Removed From Favorites";

        @Override
        protected Void doInBackground(String... title) {
            Log.v(LOG_TAG, "Removing From favorites");

            Uri favoriteTableUri = MovieContract.FavoritesEntry.CONTENT_URI;

            String[] selectionArgs = {title[0]};
            getContext().getContentResolver().delete(favoriteTableUri, MovieContract.FavoritesEntry.TITLE, selectionArgs);

            return null;
        }
        @Override
        protected void onPostExecute(Void voids) {
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            isFavorite = false;
            onFavoriteChanged();
            toast.show();
        }
    }

    private class GetTrailerTask extends AsyncTask<String, Void, Trailer[]>{
        final String ERROR_TAG = "Error in GetTrailerTask";
        @Override
        protected Trailer[] doInBackground(String... strings) {
            Log.v(LOG_TAG, "Fetching Trailers");
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                inputStream = connection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if ( inputStream == null){ //Problem in stream, no point in parsing
                    Log.e(ERROR_TAG, "Input Stream Null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ( (line = reader.readLine()) != null ){
                    builder.append(line + '\n');
                }
                if(builder.length() == 0){
                    Log.e(ERROR_TAG, "String Empty");
                    return null;
                }
                return getInfoFromJSON(builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(ERROR_TAG, "Error in closing reader");
                    }
                }
            }
            Log.e(ERROR_TAG, "Other Error");
            return null;

        }
        @Override
        protected void onPostExecute(Trailer[] trailers ){
            setUpTrailers(trailers);
        }
        private Trailer[] getInfoFromJSON(String JSONString){
            if (JSONString == null){
                return null;
            }
            try {
                JSONObject jsonObject = new JSONObject(JSONString);
                JSONArray results = jsonObject.getJSONArray("results");
                ArrayList<Trailer> trailers = new ArrayList<Trailer>();
                for (int i = 0; i< results.length(); i++){
                    JSONObject trailerObject = results.getJSONObject(i); //Note that this app only supports english
                    if (trailerObject.getString("iso_639_1").equals("en") // since the database is mostly english
                            && trailerObject.getString("iso_3166_1").equals("US")
                            && trailerObject.getString("site").equals("YouTube")
                            && (trailerObject.getString("type").equals("Teaser")
                            || trailerObject.getString("type").equals("Trailer")) ){
                        trailers.add(new Trailer(trailerObject.getString("key"), trailerObject.getString("name")));
                    }
                }

                if (trailers.size() != 0) {
                    Trailer t = trailers.get(0);
                    Log.v(LOG_TAG, t.toString());
                    Trailer[] trailers1 = new Trailer[trailers.size()];
                    for (int i =0; i<trailers.size(); i++){
                        trailers1[i] = trailers.get(i);
                    }
                    return trailers1;
                }
                return null;
            } catch (JSONException e) {
                Log.e(ERROR_TAG, "Error parsing JSON");
                return null;
            }
        }
    }

}
