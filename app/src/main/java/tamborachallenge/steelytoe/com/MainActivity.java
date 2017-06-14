package tamborachallenge.steelytoe.com;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import tamborachallenge.steelytoe.com.common.Impl.CrudTempDeliverySmsFailed;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempDeliverySmsSent;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempLocationImpl;
import tamborachallenge.steelytoe.com.ui.activity.FileGpxActivity;
import tamborachallenge.steelytoe.com.ui.activity.list.ListViewLoc;
import tamborachallenge.steelytoe.com.ui.activity.list.ListViewSmsFailed;
import tamborachallenge.steelytoe.com.ui.activity.list.ListViewSmsSent;
import tamborachallenge.steelytoe.com.ui.fragments.display.GpsDetailViewFragment;
import tamborachallenge.steelytoe.com.ui.fragments.settings.OpenGTSFragment;
import tamborachallenge.steelytoe.com.ui.fragments.settings.PerformanceSettingsFragment;
import tamborachallenge.steelytoe.com.ui.fragments.settings.SmsFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadDefaultFragmentView();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermission()) {
            requestPermission();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        int id = item.getItemId();
        if (id == R.id.sms_sent) {
            startActivity(new Intent(this, ListViewSmsSent.class));
        } else if (id == R.id.sms_failed){
            startActivity(new Intent(this, ListViewSmsFailed.class));
        } else if (id == R.id.loc_log){
            startActivity(new Intent(this, ListViewLoc.class));
        } else if (id == R.id.reset_log){
            createAndShowAlertDialog();
        }

        transaction.commitAllowingStateLoss();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this, PerformanceSettingsFragment.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, FileGpxActivity.class));
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this, OpenGTSFragment.class));
        } else if (id == R.id.nav_send) {
            startActivity(new Intent(this, SmsFragment.class));
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //=============================
    private void loadDefaultFragmentView() {
        int currentSelectedPosition = 0;
        loadFragmentView(currentSelectedPosition);
    }

    private void loadFragmentView(int position){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (position) {
            default:
            case 0:
                /*transaction.replace(R.id.container_2, GpsMapsFragment.newInstance());*/
                transaction.replace(R.id.container_2, GpsDetailViewFragment.newInstance());
                break;
        }
        transaction.commitAllowingStateLoss();
    }


    // ============================================ RESET
    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Reset").setMessage("Are you sure you reset the log location ?");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete db location sqlite
                CrudTempLocationImpl crudTempLocation = new CrudTempLocationImpl(MainActivity.this);
                crudTempLocation.deleteAll();

                //delete db sms sent in sqlite
                CrudTempDeliverySmsSent crudTempDeliverySmsSent = new CrudTempDeliverySmsSent(MainActivity.this);
                crudTempDeliverySmsSent.deleteAll();

                //delete db sms failed in sqlite
                CrudTempDeliverySmsFailed crudTempDeliverySmsFailed = new CrudTempDeliverySmsFailed(MainActivity.this);
                crudTempDeliverySmsFailed.deleteAll();

                //delete file gpx
                File myDir = new File(getFilesDir().getAbsolutePath());
                File file = new File(myDir, "log.gpx");

                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialogInterface = builder.create();
        dialogInterface.show();
    }

    private boolean checkPermission() {
        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int sendSmsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int readSmsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int receiveSmsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readPhoneStatePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);


        return locationPermission == PackageManager.PERMISSION_GRANTED
                && cameraPermission == PackageManager.PERMISSION_GRANTED
                && sendSmsPermission == PackageManager.PERMISSION_GRANTED
                && readSmsPermission == PackageManager.PERMISSION_GRANTED
                && receiveSmsPermission == PackageManager.PERMISSION_GRANTED
                && readPhoneStatePermission == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        boolean shouldShowPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        final String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE};

        if (shouldShowPermissionRationale) {
            Snackbar.make(findViewById(R.id.content_main), getString(R.string.permission_rationale),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OKE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions,
                                    REQUEST_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .show();

        }else {
            ActivityCompat.requestPermissions(MainActivity.this, permissions,
                    REQUEST_PERMISSION_REQUEST_CODE);
        }

    }
}
