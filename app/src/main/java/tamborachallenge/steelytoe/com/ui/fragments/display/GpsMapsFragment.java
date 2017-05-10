package tamborachallenge.steelytoe.com.ui.fragments.display;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.events.ServiceBackground;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempLocationImpl;
import tamborachallenge.steelytoe.com.common.events.ServiceSendSms;
import tamborachallenge.steelytoe.com.common.events.ServiceSmsFailed;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lat;
import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lng;

public class GpsMapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = GpsMapsFragment.class.getSimpleName();
    private View rootView;
    private GoogleMap mMap;
    private LatLng latLngLocation;
    private List<Marker> originMarkers = new ArrayList<>();
    Marker marker;

    ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    ArrayList<LatLng> coordRoute = new ArrayList<LatLng>();

    Button btnCal, btnProses;

    public static GpsMapsFragment newInstance() {
        GpsMapsFragment fragment = new GpsMapsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnCal = (Button) rootView.findViewById(R.id.btnCal);
        /*btnReset = (Button) rootView.findViewById(R.id.btnReset);*/
        btnProses = (Button) rootView.findViewById(R.id.btnProses);

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordList.clear();
                coordRoute.clear();
                polylineLocationOffline();
            }
        });

        /*btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndShowAlertDialog();
            }
        });*/

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadService();
            }
        });

        /*new loadJSONFromAsset().execute();*/

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String value = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(getActivity()," "+ value, Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(),"Scanning Gagal, mohon coba lagi.", Toast.LENGTH_SHORT).show();
            }
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadService(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(checkGps() == 0 ) {
            Toast.makeText( getActivity(), "Gps Disable", Toast.LENGTH_SHORT).show();
        } else {
            if (checkSession() == 1) {
                // Proses Stop
                editor.putString("session", "0");
                editor.commit();
                setActionButtonStart();
                Intent service = new Intent(getActivity(), ServiceBackground.class);
                getActivity().stopService(service);

                Intent serviceSmsSend = new Intent(getActivity(), ServiceSendSms.class);
                getActivity().stopService(serviceSmsSend);

//                Intent serviceSmsFailed = new Intent(getActivity(), ServiceSmsFailed.class);
//                getActivity().stopService(serviceSmsFailed);

            } else if (checkSession() == 0) {
                // Proses Start
                editor.putString("session", "1");
                editor.commit();
                setActionButtonStop();
                Intent service = new Intent(getActivity(), ServiceBackground.class);
                getActivity().startService(service);

                Intent serviceSmsSend = new Intent(getActivity(), ServiceSendSms.class);
                getActivity().startService(serviceSmsSend);

//                Intent serviceSmsFailed = new Intent(getActivity(), ServiceSmsFailed.class);
//                getActivity().stopService(serviceSmsFailed);

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkSession()  == 0){
            setActionButtonStart();
        } else {
            setActionButtonStop();
        }
    }

    // Button
    private void setActionButtonStart() {
        btnProses.setText(R.string.btn_start_logging);
    }

    private void setActionButtonStop() {
        btnProses.setText(R.string.btn_stop_logging);
    }


    /// ===============================================================================
    void polylineLocationOffline() {
        /*mMap.clear();*/
        double latitude = 0, longitude = 0;

        CrudTempLocationImpl crudTempLocation = new CrudTempLocationImpl(getActivity());
        ArrayList<HashMap<String, String>> locationList = crudTempLocation.getLocation();
        if (locationList.size() != 0) {
            for (Map<String, String> location : locationList) {
                latitude = Double.parseDouble(location.get(KEY_lat));
                longitude = Double.parseDouble(location.get(KEY_lng));
                coordList.add(new LatLng(latitude, longitude));
            }
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(coordList);
            polylineOptions
                    .width(5)
                    .color(Color.BLUE);
            mMap.addPolyline(polylineOptions);
            markerLocationPeople(latitude, longitude);
        } else {
            Toast.makeText(getActivity(), "No location!", Toast.LENGTH_SHORT).show();
        }
    }

    public void markerLocationPeople(double latLast, double lngLast) {
        latLngLocation = new LatLng(latLast, lngLast);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 9));
        marker = mMap.addMarker(new MarkerOptions().position(latLngLocation)
                .title("I am Here"));
        marker.remove();
    }

    public void markerLocationCenter(double latLast, double lngLast, String name) {
        latLngLocation = new LatLng(latLast, lngLast);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 9));

        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(latLngLocation)));
    }

    public void markerLocationStart(double latLast, double lngLast, String name) {
        latLngLocation = new LatLng(latLast, lngLast);
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 9));*/

        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(latLngLocation)));
    }

    public void markerLocationFinish(double latLast, double lngLast, String name) {
        latLngLocation = new LatLng(latLast, lngLast);
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 18));*/
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(latLngLocation)));
    }

    public void markerLocationPoint(double latLast, double lngLast, String name) {
        latLngLocation = new LatLng(latLast, lngLast);
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 19));*/
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(latLngLocation)));
    }

    public void markerLocationCP(double latLast, double lngLast, String name) {
        latLngLocation = new LatLng(latLast, lngLast);
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 19));*/
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .position(latLngLocation)));

    }



    // ===================================================================================== Check STATUS and GPS
    private final int checkSession(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String session = sharedPreferences.getString("session", null);
        int stsSession = 0;
        if(session == null || session == ""){
            editor.putString("session", "0");
            editor.commit();
        }else{
            stsSession = Integer.parseInt(session);
        }
        return stsSession;
    }

    private final int checkGps(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        int mode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        return mode;
    }
    // =========== Check GPS END


    // ===================================================================================== On MAPREADY
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }
    // ===========On MAPREADY END


    // ===================================================================================== READ FILE ROUTE.JSON
    class loadJSONFromAsset extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(Void... urls) {
            String json = null;
            try {
                InputStream is = getActivity().getAssets().open("route.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                json = new String(buffer, "UTF-8");
                return json.toString();
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);
            try {

                JSONObject jsonObj = new JSONObject(response);

                // =============================== Getting JSON Array path
                JSONArray arrayPath = jsonObj.getJSONArray("path");
                String[] arr=new String[arrayPath.length()];
                for (int i = 0; i < arrayPath.length(); i++) {
                    arr[i]=arrayPath.getString(i);
                }
                for (int j = 0; j < arr.length; j++){
                    String hasil = arr[j];
                    String [] split = hasil.split(",");
                    String ke0 = split[0].replaceAll("\"", "").replace("[", "");
                    String ke1 = split[1].replace("\"", "").replace("]", "");

                    double lat = Double.parseDouble(ke0);
                    double lng = Double.parseDouble(ke1);
                    coordRoute.add(new LatLng(lat, lng));

                    System.out.println("Data Ke-"+(j+1)+" : " + ke0 + "--" + ke1);
                }
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(coordRoute);
                polylineOptions
                        .width(5)
                        .color(Color.RED);
                mMap.addPolyline(polylineOptions);

                // =============================== Getting JSON Array attribute
                JSONArray arrayAttribute = jsonObj.getJSONArray("attribute");
                for (int i = 0; i < arrayAttribute.length(); i++) {
                    JSONObject c = arrayAttribute.getJSONObject(i);
                    String name = c.getString("name");

                    String pos = c.getString("pos");
                    String [] split = pos.split(",");
                    String ke0 = split[0].replaceAll("\"", "").replace("[", "");
                    String ke1 = split[1].replace("\"", "").replace("]", "");
                    double lat = Double.parseDouble(ke0);
                    double lng = Double.parseDouble(ke1);

                    String type = c.getString("type");

                    int t = Integer.parseInt(type);

                    if(t == 1){
                        markerLocationPoint(lat,lng, name);
                    } else if(t == 2){
                        markerLocationCP(lat,lng, name);
                    } else if(t == 4){
                        markerLocationFinish(lat,lng, name);
                    } else if(t == 3){
                        markerLocationStart(lat,lng, name);
                    } else if(t == 6){
                        markerLocationCenter(lat,lng, name);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // ===========READ FILE ROUTE.JSON  END


}
