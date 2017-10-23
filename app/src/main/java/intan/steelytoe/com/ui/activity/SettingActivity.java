package intan.steelytoe.com.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import intan.steelytoe.com.R;
import intan.steelytoe.com.ui.fragments.settings.SettingFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getFragmentManager().beginTransaction()
                .add(R.id.container_setting, new SettingFragment())
                .commit();
    }
}
