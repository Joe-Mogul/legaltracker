package com.javaapps.legaltracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LegalTrackerActivity extends Activity {

	private static LegalActivityUpdater receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.monitor);
		Intent i = new Intent();
		i.setAction("kickOffLogger");
		this.sendBroadcast(i);
		registerActivityReceiver();
		updateUI();
		Button refreshMonitorButton = (Button) this
				.findViewById(R.id.RefreshMonitor);
		refreshMonitorButton.setOnClickListener(new RefreshMonitorListener());

		Log.i("legaltracker", "activity kicked off");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.javaapps.legaltracker.R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.configureLegalTracker:
			Intent configurationIntent = new Intent(this,
					ConfigurationActivity.class);
			startActivity(configurationIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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
		com.javaapps.legaltracker.Monitor monitor = Monitor.getInstance();
		TextView statusView = (TextView) findViewById(R.id.Status);
		statusView.setText(monitor.getStatus());
		TextView lastLocationView = (TextView) findViewById(R.id.Location);
		lastLocationView.setText(monitor.getLastLocation());
		TextView lastUploadView = (TextView) findViewById(R.id.Processed);
		lastUploadView.setText(monitor.getLastUploadDateDisplay());
		TextView archivesView = (TextView) findViewById(R.id.Archives);
		archivesView.setText(monitor.getArchiveFiles());
		TextView lastStatusCodeView = (TextView) findViewById(R.id.LastStatusCode);
		lastStatusCodeView.setText(String.valueOf(monitor.getLastUploadStatusCode()));
	}

	public class LegalActivityUpdater extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			updateUI();
		}

	}

	private class RefreshMonitorListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			LegalTrackerActivity.this.updateUI();
		}
	}
}
