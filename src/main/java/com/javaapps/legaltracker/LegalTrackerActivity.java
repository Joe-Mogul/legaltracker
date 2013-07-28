package com.javaapps.legaltracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class LegalTrackerActivity extends Activity {

	private static LegalActivityUpdater receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent i = new Intent();
		i.setAction("kickOffLogger");
		this.sendBroadcast(i);
		registerActivityReceiver();
		updateUI();
		Log.i("legaltracker", "activity kicked off");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.javaapps.legaltracker.R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void registerActivityReceiver() {
		receiver = new LegalActivityUpdater();
		IntentFilter filter = new IntentFilter(
				" com.javaapps.legaltracker.LegalActivityUpdater ");
		try {
			registerReceiver(receiver, filter);
		} catch (Exception ex) {
			Log.e("legal tracker error", "Unable to register receiver because "
					+ ex.getMessage());
		}
	}

	private void updateUI() {
		Log.i("legaltrackermonitor", "Legal Tracker Activity has been resumed");
		Monitor monitor = Monitor.getInstance();
		TextView statusView = (TextView) findViewById(R.id.Status);
		statusView.setText(monitor.getStatus());
		TextView lastLocationView = (TextView) findViewById(R.id.Location);
		lastLocationView.setText(monitor.getLastLocation());
		TextView lastUploadView = (TextView) findViewById(R.id.Processed);
		lastUploadView.setText(monitor.getLastUploadDateDisplay());
	}

	public class LegalActivityUpdater extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			updateUI();
		}

	}
}
