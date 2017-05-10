package tamborachallenge.steelytoe.com.ui.fragments.settings;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.os.Build;
import android.preference.Preference;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;
import com.afollestad.materialdialogs.prefs.MaterialListPreference;
import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.senders.PreferenceValidator;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class OpenGTSFragment extends android.preference.PreferenceActivity implements PreferenceValidator,
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private static final String TAG = OpenGTSFragment.class.getSimpleName();

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.opengtssettings);

        findPreference("opengts_server").setOnPreferenceChangeListener(this);
        findPreference("opengts_server_port").setOnPreferenceChangeListener(this);
        findPreference("opengts_server_communication_method").setOnPreferenceChangeListener(this);
        findPreference("autoopengts_server_path").setOnPreferenceChangeListener(this);
        findPreference("opengts_device_id").setOnPreferenceChangeListener(this);
        findPreference("opengts_validatecustomsslcert").setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (!isFormValid()) {
            Toast.makeText(this, "Data Tidak Valid ", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(this, "Data Valid ", Toast.LENGTH_SHORT).show();
        return true;
    }


    private boolean isFormValid() {

        MaterialEditTextPreference txtOpenGTSServer = (MaterialEditTextPreference) findPreference("opengts_server");
        MaterialEditTextPreference txtOpenGTSServerPort = (MaterialEditTextPreference) findPreference("opengts_server_port");
        MaterialListPreference txtOpenGTSCommunicationMethod = (MaterialListPreference) findPreference("opengts_server_communication_method");
        MaterialEditTextPreference txtOpenGTSServerPath = (MaterialEditTextPreference) findPreference("autoopengts_server_path");
        MaterialEditTextPreference txtOpenGTSDeviceId = (MaterialEditTextPreference) findPreference("opengts_device_id");

        return  txtOpenGTSServer.getText() != null && txtOpenGTSServer.getText().length() > 0
                && txtOpenGTSServerPort.getText() != null && isNumeric(txtOpenGTSServerPort.getText())
                && txtOpenGTSCommunicationMethod.getValue() != null && txtOpenGTSCommunicationMethod.getValue().length() > 0
                && txtOpenGTSDeviceId.getText() != null && txtOpenGTSDeviceId.getText().length() > 0
                && URLUtil.isValidUrl("http://" + txtOpenGTSServer.getText() + ":" + txtOpenGTSServerPort.getText() + txtOpenGTSServerPath.getText());

    }

    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValid() {
        return isFormValid();
    }
}
