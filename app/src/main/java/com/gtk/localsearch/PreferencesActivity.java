package com.gtk.localsearch;

import android.os.Bundle;
import android.preference.PreferenceActivity;



/**
 * Created by RISHAB on 26-01-2017.
 */

public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);
    }
}
