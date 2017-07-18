package com.example.traviswilson.popularmoviesstagetwo.activities;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.traviswilson.popularmoviesstagetwo.R;
import com.example.traviswilson.popularmoviesstagetwo.adapters.ReviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.acl.LastOwnerException;

/**
 * Created by traviswilson on 6/10/17.
 */

public class ReviewFragment extends Fragment {
    private static final String LOG_TAG = "Review Fragment";
    ReviewAdapter reviewAdapter;
    String reviewsUri;
    ListView mListView;
    boolean isTwoPane;
    boolean rotationFlag = false;

    /**
     * A container class for reviews
     */
    public class ReviewFile {
        private String author;
        private String reviewContent;
        ReviewFile(){
            this(null,null);
        }
        ReviewFile(String author, String reviewContent){
            this.author = author;
            this.reviewContent = reviewContent;
        }
        public String getAuthor(){
            return author;
        }
        public String getReviewContent(){
            return reviewContent;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            rotationFlag = true;
        } else{
            rotationFlag = false;
        }
        setRetainInstance(true);
        try {
            MainActivity activity = (MainActivity) getActivity();
            reviewsUri = getArguments().getString(DetailFragment.REVIEW_URL_STRING_TAG);
            isTwoPane = true;
        } catch (ClassCastException e){
            isTwoPane = false;
            reviewsUri= getArguments().getString(DetailFragment.REVIEW_URL_STRING_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        new RetrieveReviewsTask().execute(reviewsUri);
        View v = inflater.inflate(R.layout.review_fragment, container, false);
        mListView = (ListView) v.findViewById(R.id.review_list_view);
        return v;
    }

    /**
     * Helper method that takes the reviews and populates the UI.
     * @param reviews the reviews retrieved by the asyncTask
     */

    private void onReviewsRetrieved(ReviewFile[] reviews){
        if(reviews != null && reviews.length != 0) {
            if (!rotationFlag) reviewAdapter = new ReviewAdapter(getActivity(), reviews);
            mListView.setAdapter(reviewAdapter);

        } else{
            Toast.makeText(getActivity(), "No reviews",Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * Class that encapsulates the retrieval of review information from the internet. Pass in the
     * URL to download it from.
     */
    private class RetrieveReviewsTask extends AsyncTask<String, Void, ReviewFile[]> {
        private final String[] JSONFields = { "author",
        "content"};

        @Override
        protected ReviewFile[] doInBackground(String...strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = null;


            try {
                URL url = new URL(strings[0]);
                Log.v(LOG_TAG, strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    //Nothing gained
                    Log.e("Error!", "Input stream null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append('\n');
                }
                if (builder.length() == 0) {
                    Log.e("Error!", "There was no content");
                    return null;
                }
                jsonString = builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getInfoFromJSON(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private ReviewFile[] getInfoFromJSON(String JSONString) throws JSONException{
            final int AUTHOR_INDEX = 0;
            final int CONTENT_INDEX = 1;

            if (JSONString == null){
                return null;
            }
            Log.v("Review Fragment", "JSON String not null");
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray results = jsonObject.getJSONArray("results");
            ReviewFile[] contentArray = new ReviewFile[results.length()];
            for (int i= 0 ; i<results.length(); i++){
                JSONObject currentData = results.getJSONObject(i);
                contentArray[i] = new ReviewFile();
                contentArray[i].author = currentData.getString(JSONFields[AUTHOR_INDEX]);
                contentArray[i].reviewContent = currentData.getString(JSONFields[CONTENT_INDEX]);
            }

            return contentArray;
        }

        @Override
        public void onPostExecute(ReviewFile[] resultsArray){
            onReviewsRetrieved(resultsArray);
        }
    }
}
