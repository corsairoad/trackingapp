package tamborachallenge.steelytoe.com.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tamborachallenge.steelytoe.com.R;

import java.io.FileInputStream;

public class FileGpxActivity extends AppCompatActivity {
    private static final String TAG = FileGpxActivity.class.getSimpleName();
    Button btnViewGpx, btnUpload;
    EditText txtViewGpx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_gpx);

        txtViewGpx = (EditText) findViewById(R.id.txtViewGpx);
        /*btnGPX= (Button) findViewById(R.id.btnGPX);*/
        btnViewGpx = (Button) findViewById(R.id.btnViewGpx);
        /*btnDelGpx = (Button) findViewById(R.id.btnDelGpx);*/
        btnUpload = (Button) findViewById(R.id.btnUpload);

        btnViewGpx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileInputStream fin = openFileInput("log.gpx");
                    int c;
                    String temp="";
                    while( (c = fin.read()) != -1){
                        temp = temp + Character.toString((char)c);
                    }
                    txtViewGpx.setText(temp.toString());
                    Log.d(TAG, "log.gpx"+ temp);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "Under Construction", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
