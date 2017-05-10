package tamborachallenge.steelytoe.com.ui.fragments.settings;

import android.os.Bundle;

import tamborachallenge.steelytoe.com.R;
/**
 * Created by dki on 01/03/17.
 */

public class PerformanceSettingsFragment extends android.preference.PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_performance);
    }
}
