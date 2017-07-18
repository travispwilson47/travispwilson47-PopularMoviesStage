package com.example.traviswilson.popularmoviesstagetwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.traviswilson.popularmoviesstagetwo.activities.DetailFragment;

import static com.example.traviswilson.popularmoviesstagetwo.activities.DetailFragment.DIALOG_ARGUMENT_KEY;

//import static com.example.traviswilson.popularmoviesstagetwo.activities.DetailFragment.DIALOG_ARGUMENT_KEY;

/**
 * Created by traviswilson on 7/4/17.
 * This class is not an inner class of DetailFragment like the PlayTrailersDialog because it needs
 * to communicate between the two fragments in order
 */

public class AddFavoriteDialog extends DialogFragment {

    private AddFavoriteDialog.Callback mListener;
    private Activity mContext;

    public interface Callback{
        void onAffButtonClicked(boolean isFavorite);
    }

    @Override
    public void onAttach(Context context){
        try {
            mContext = (Activity) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() +"Must be activity");
        }
        try {
            mListener = (AddFavoriteDialog.Callback) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() +"Must implement AddFavoriteDialog" +
                    ".CallBack ");
        }
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState){
        final Boolean isFavorite = getArguments().getBoolean(DIALOG_ARGUMENT_KEY);

        String dialogMessage = isFavorite ? "Remove from Favorites?" : "Add to Favorites?" ;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(dialogMessage);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DetailFragment.changefavoriteButtonImage(isFavorite, mContext);
                mListener.onAffButtonClicked(isFavorite);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Method automatically dismisses dialog
            }
        });
        return builder.create();
    }
}


