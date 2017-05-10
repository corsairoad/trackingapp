package tamborachallenge.steelytoe.com.ui.fragments.display;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempLocationImpl;
import tamborachallenge.steelytoe.com.model.TempLoaction;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lat;
import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lng;
import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_timer;

public class GpsLogViewFragment extends Fragment {
    private View rootView;
    LinearLayout containerLog;
    Button btnViewLog, btnReset;
    ArrayList<TempLoaction> locationList = new ArrayList<TempLoaction>();

    public static GpsLogViewFragment newInstance() {
        GpsLogViewFragment fragment = new GpsLogViewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gps_log_view, container, false);
        containerLog = (LinearLayout) rootView.findViewById(R.id.containerLog);
        ButterKnife.bind(this, rootView);

        btnViewLog = (Button) rootView.findViewById(R.id.btnView);
        btnReset = (Button) rootView.findViewById(R.id.btnReset);

        btnViewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CrudTempLocationImpl crudTempLocation = new CrudTempLocationImpl(getActivity());
                ArrayList<HashMap<String, String>> locationList =  crudTempLocation.getLocation();
                if(locationList.size()!=0) {

                    for (Map<String, String> location : locationList) {
                        String timer = location.get(KEY_timer);
                        double latitude = Double.parseDouble(location.get(KEY_lat));
                        double longitude = Double.parseDouble(location.get(KEY_lng));
                        Log.d("LatLng ", "" + timer + " lat " + latitude + " lng " + longitude);
                    }

                }else{
                    Toast.makeText(getActivity(),"No location!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrudTempLocationImpl crudTempLocation = new CrudTempLocationImpl(getActivity());
                crudTempLocation.deleteAll();
                Toast.makeText(getActivity(), "Location Record Deleted ALL", Toast.LENGTH_SHORT);
            }
        });

        return rootView;

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
    public void onDestroy() {
        super.onDestroy();
    }

    // =========================== EventBus
    // ====================================

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(Message.LocationUpdate locationUpdate){
//        displayLocationInfo(locationUpdate.location, locationUpdate.timer);
//    }
//
    public void displayLocationInfo(Location location, String timer) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row, null);
        TextView txtValue = (TextView) view.findViewById(R.id.textContainer);

        if(location != null){
            txtValue.setText(String.valueOf("@" + timer +
                    "     lat " + location.getLatitude() +
                    "     lng " + location.getLongitude() +
                    "     alt " + location.getAltitude() +
                    "     acr " + location.getAccuracy() +
                    "         " + location.getProvider()
            ));
        }
        containerLog.addView(view);

    }

}
