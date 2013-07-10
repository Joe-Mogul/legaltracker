package com.javaapps.legaltracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class HelloAndroidActivity extends Activity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView latLonTextView = (TextView) this.findViewById(R.id.latLonBox);
        latLonTextView.setText("start app");
        try
        {
        LocationManager locationManager = (LocationManager) 
				getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latLonTextView.setText("lat lon is"+location.getLatitude()+" "+location.getLongitude());
        }catch(Exception ex){
        	latLonTextView.setText("cant get lat long because "+ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(com.javaapps.legaltracker.R.menu.main, menu);
	return true;
    }

}
