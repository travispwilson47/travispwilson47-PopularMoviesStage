package com.example.traviswilson.popularmoviesstagetwo;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.traviswilson.popularmoviesstagetwo.data.MovieContract;

import static com.example.traviswilson.popularmoviesstagetwo.data.MovieContract.FavoritesEntry.buildFavoriteUri;

/**
 * Created by traviswilson on 12/25/16. Class with a variety of methods that help factoring out code
 */
public class Utility {
    /**
     * Method simply sends back the preferred display stored in settings
     * @param context
     * @return
     */
    public static String getPreferredDisplayType(Context context){
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.sort_key),
                        context.getString(R.string.pop_value));
    }

    /**
     * Method returns row modified URI, given the row number, that may be used for a specific
     * Query of a single row
     * @param context
     * @param rowIDNumber
     * @return
     */
    public static Uri getTableVectorUri(Context context, int rowIDNumber ) {
        String displaySetting = Utility.getPreferredDisplayType(context);
        switch (displaySetting) {
            case "favorite":
                return buildFavoriteUri(rowIDNumber);
            case "top rated":
                return MovieContract.MovieBestEntry.buildBestUri(rowIDNumber);
            case "most popular":
                return MovieContract.MoviePopularEntry.buildPopularUri(rowIDNumber);
            default:
                Log.v("Utility eror", displaySetting);
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Method returns a Uri for table appropriate to current Display setting
     * @param context to use to call preferred type
     * @return the generated Uri
     */
    public static Uri getTableUri(Context context){
        String displaySetting = Utility.getPreferredDisplayType(context);
        switch (displaySetting){
            case "favorite":
                return MovieContract.FavoritesEntry.CONTENT_URI;
            case "top rated":
                return MovieContract.MovieBestEntry.CONTENT_URI;
            case "most popular":
                return MovieContract.MoviePopularEntry.CONTENT_URI;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * returns DP value of screen width
     * @param context to use to get displayMetrics
     * @return DP value of screenWidth
     */
    public static float getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return dpFromPx(context, width);
    }

}
