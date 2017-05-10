package tamborachallenge.steelytoe.com.ui.fragments.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;

import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.senders.PreferenceValidator;

/**
 * Created by dki on 01/03/17.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class SmsFragment extends android.preference.PreferenceActivity implements PreferenceValidator,
    Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.smssetting);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
