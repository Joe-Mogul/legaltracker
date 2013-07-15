package com.javaapps.legaltracker;

import java.util.Date;

import com.javaapps.legaltracker.receiver.LegalTrackerKickOffReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class LegalTrackerActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView latLonTextView = (TextView) this.findViewById(R.id.latLonBox);
		latLonTextView.setText("started service");
		Intent i = new Intent();
		i.setAction("kickOffLogger");
		this.sendBroadcast(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.javaapps.legaltracker.R.menu.main, menu);
		return true;
	}

	
}
