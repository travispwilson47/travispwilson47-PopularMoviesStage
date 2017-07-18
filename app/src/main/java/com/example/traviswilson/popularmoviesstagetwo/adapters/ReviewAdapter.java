package com.example.traviswilson.popularmoviesstagetwo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.traviswilson.popularmoviesstagetwo.R;
import com.example.traviswilson.popularmoviesstagetwo.activities.ReviewFragment;

/**
 * Created by traviswilson on 6/10/17.
 */

public class ReviewAdapter extends ArrayAdapter<ReviewFragment.ReviewFile> {

    private final String APPEND_TO_AUTHOR_TEXT = " Says:";
    private static final String LOG_TAG = "Review Adapter";

    public ReviewAdapter(@NonNull Context context,ReviewFragment.ReviewFile[] reviewFiles) {
        super(context, 0, reviewFiles);
        Log.v(LOG_TAG, "There are "+ reviewFiles.length+"reviews");
    }
    /**
     * ViewHolder for the two subviews in an individual reviewItem
     */
    private static class ViewHolder {
        private final TextView author;
        private final TextView reviewContent;

            ViewHolder(View view) {
            author = (TextView) view.findViewById(R.id.review_author);
            reviewContent = (TextView) view.findViewById(R.id.review_content);
        }
    }

    /**
     *
     * @param position of current cycle, used to get the reviewFile (container for info)
     * @param convertView view to be converted
     * @param parent parent view to be used for layout inflation
     * @return converted view
     */
    @Override
    public View getView(int position, View convertView , ViewGroup parent){
        ReviewFragment.ReviewFile file = getItem(position);
        if (file == null) {
            Log.v("Review Adapter", "Null File somehow");
            throw new RuntimeException();
        } else{
            Log.v("Review Adapter", "We are ok");
        }
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder =  ((ViewHolder) convertView.getTag());
        viewHolder.author.setText(file.getAuthor() + APPEND_TO_AUTHOR_TEXT); //Translation not a target in this app.
        viewHolder.reviewContent.setText(file.getReviewContent());

        return convertView;
    }

}
