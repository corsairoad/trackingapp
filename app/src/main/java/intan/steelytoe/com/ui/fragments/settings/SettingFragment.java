package intan.steelytoe.com.ui.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import intan.steelytoe.com.R;

/**
 * Created by fadlymunandar on 6/28/17.
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private EditTextPreference editTextPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences2);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        int preferencesCount = getPreferenceScreen().getPreferenceCount();
        for(int i=0; i < preferencesCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceCategory) {
                PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
                for (int j=0; j < preferenceCategory.getPreferenceCount(); j++) {
                    Preference singlePref = preferenceCategory.getPreference(j);
                    updatePreference(singlePref, singlePref.getKey());
                }
            }else {
                updatePreference(preference, preference.getKey());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        updatePreference(preference, key);
    }

    private void updatePreference(Preference preference, String key) {

        if (preference == null) return;

        String title = preference.getTitle().toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
            return;
        }
        SharedPreferences sharedPreference = getPreferenceScreen().getSharedPreferences();
        preference.setSummary(sharedPreference.getString(key, "Set " + title));
    }
}
