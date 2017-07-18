package com.example.traviswilson.popularmoviesstagetwo.settings;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.example.traviswilson.popularmoviesstagetwo.R;

/**
 * Created by traviswilson on 12/25/16. Class is just a shell for settings fragment
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new SettingsFragment()).commit();
    }

    /**
     * Method for making sure that if the menu option for selecting movies is enabled, the prefrence
     * for changing it is disabled. Note that there will be deprecated methods used, as this is
     * activity for API level 11 and below
     * @param preference
     * @param value
     * @return success
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference instanceof CheckBoxPreference){
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.sort_key));
            if (checkBoxPreference.isEnabled()){
                listPreference.setEnabled(false);
            } else{
                listPreference.setEnabled(true);
            }
        }
        return true;
    }

}