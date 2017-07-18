package com.example.traviswilson.popularmoviesstagetwo.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.traviswilson.popularmoviesstagetwo.R;

/**
 * Created by traviswilson on 12/25/16. Settings fragment class. Just adds from preference xml file.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
