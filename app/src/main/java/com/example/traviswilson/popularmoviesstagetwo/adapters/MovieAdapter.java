package com.example.traviswilson.popularmoviesstagetwo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.traviswilson.popularmoviesstagetwo.activities.MainFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by traviswilson on 1/8/17. Custom Adapter with Viewholder pattern for the array of movies.
 * Assumes the existence of a cursor and a database.
 *
 */
public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    public void changeSelected(String newSelectedTitle){
        swapCursor(getCursor()); //resets the cursor adapter, so that the selected item is now selected
    }

    /**
     * We override this method here to make sure that when a valid cursor is passed there is
     * always some form of selected item, that can be changed when the user selects an item
     * @param cursor cursor to be swapped
     * @return same cursor for chaining
     */
    @Override
    public Cursor swapCursor(Cursor cursor){
        if (cursor!= null){
            cursor.moveToFirst();
        }
        super.swapCursor(cursor);
        return cursor;
    }
    /**
     * In this case, we just have an image view so we create a new one.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return new ImageView(context);
    }
    /**
     * Here we work with the view itself
     * Note that although we read the url every time, it does not load every time since
     * Picasso handles caches and such.
     *
     * Additionally, if we find that the current view is that which is selected, then we
     * programmatically change the image view to show it as selected visually.
     */
    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        String URLString = cursor.getString(MainFragment.COL_IMAGE_FILE);

        //Code for checking if this image is selected
        Picasso.with(context).load(URLString).into((ImageView) view);
        ((ImageView) view).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ((ImageView) view).setAdjustViewBounds(true);


    }
}
