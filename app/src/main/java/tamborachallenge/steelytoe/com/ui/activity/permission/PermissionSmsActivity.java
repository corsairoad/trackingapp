package tamborachallenge.steelytoe.com.ui.activity.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import tamborachallenge.steelytoe.com.R;

public class PermissionSmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_sms);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
        } else {
            startActivity(new Intent(this, PermissionCameraActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, PermissionCameraActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Permission Not Granted.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
