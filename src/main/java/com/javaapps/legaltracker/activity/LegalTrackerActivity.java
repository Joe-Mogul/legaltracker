package com.javaapps.legaltracker.activity;

import com.javaapps.legaltracker.R;
import com.javaapps.legaltracker.R.id;
import com.javaapps.legaltracker.R.layout;
import com.javaapps.legaltracker.R.menu;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.Monitor;

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

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.monitor);
		Intent i = new Intent();
		i.setAction("kickOffLogger");
		this.sendBroadcast(i);
		updateUI();
		Button refreshMonitorButton = (Button) this
				.findViewById(R.id.RefreshMonitor);
		refreshMonitorButton.setOnClickListener(new RefreshMonitorListener());
		Button resetMonitorButton = (Button) this
				.findViewById(R.id.ResetMonitor);
		resetMonitorButton.setOnClickListener(new ResetMonitorListener());

		Log.i(Constants.LEGAL_TRACKER_TAG, "activity kicked off");
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
	}



	private void updateUI() {
		Log.i("legaltrackermonitor", "Legal Tracker Activity has been resumed");
		com.javaapps.legaltracker.pojos.Monitor monitor = Monitor.getInstance();
		TextView statusView = (TextView) findViewById(R.id.Status);
		statusView.setText(monitor.getStatus());
		TextView wifiStatusView = (TextView) findViewById(R.id.WifiStatus);
		wifiStatusView.setText(monitor.getWifiStatus());
		TextView gpsStatusView = (TextView) findViewById(R.id.GpsStatus);
		gpsStatusView.setText(monitor.getGpsStatus());
		TextView lastConnectionErrorView = (TextView) findViewById(R.id.LastConnectionError);
		lastConnectionErrorView.setText(monitor.getLastConnectionError());
		TextView lastGForceConnectionErrorView = (TextView) findViewById(R.id.LastGForceConnectionError);
		lastGForceConnectionErrorView.setText(monitor.getLastGForceConnectionError());
		TextView lastLocationView = (TextView) findViewById(R.id.Location);
		lastLocationView.setText(monitor.getLastLocation());
		TextView loggedPointsView = (TextView) findViewById(R.id.Logged);
		loggedPointsView.setText(String.valueOf(monitor.getTotalPointsLogged()));
		TextView loggedGForcePointsView = (TextView) findViewById(R.id.GForceLogged);
		loggedGForcePointsView.setText(String.valueOf(monitor.getTotalGForcePointsLogged()));
		TextView uploadedPointsView = (TextView) findViewById(R.id.Uploaded);
		uploadedPointsView.setText(String.valueOf(monitor.getTotalPointsUploaded()));
		TextView uploadedGForcePointsView = (TextView) findViewById(R.id.GForceUploaded);
		uploadedGForcePointsView.setText(String.valueOf(monitor.getTotalGForcePointsUploaded()));
		TextView procesedPointsView = (TextView) findViewById(R.id.Processed);
		procesedPointsView.setText(String.valueOf(monitor.getTotalPointsProcessed()));
		TextView procesedGForcePointsView = (TextView) findViewById(R.id.GForceProcessed);
		procesedGForcePointsView.setText(String.valueOf(monitor.getTotalGForcePointsProcessed()));
		TextView notProcesedPointsView = (TextView) findViewById(R.id.NotProcessed);
		notProcesedPointsView.setText(String.valueOf(monitor.getTotalPointsNotProcessed()));
		TextView notProcesedGForcePointsView = (TextView) findViewById(R.id.GForceNotProcessed);
		notProcesedGForcePointsView.setText(String.valueOf(monitor.getTotalGForcePointsNotProcessed()));
		TextView archivesView = (TextView) findViewById(R.id.Archives);
		archivesView.setText(monitor.getArchiveFiles());
		TextView lastStatusCodeView = (TextView) findViewById(R.id.LastStatusCode);
		lastStatusCodeView.setText(String.valueOf(monitor.getLastUploadStatusCode()));
		TextView lastGForceStatusCodeView = (TextView) findViewById(R.id.LastGForceStatusCode);
		lastGForceStatusCodeView.setText(String.valueOf(monitor.getLastGForceUploadStatusCode()));
		TextView currentFileSizeView = (TextView) findViewById(R.id.CurrentFileSize);
		currentFileSizeView.setText(String.valueOf(monitor.getCurrentFileSize()));
		TextView currentGForceFileSizeView = (TextView) findViewById(R.id.CurrentGForceFileSize);
		currentGForceFileSizeView.setText(String.valueOf(monitor.getGforceFileSize()));
		TextView pointsInBufferView = (TextView) findViewById(R.id.PointsInBuffer);
		pointsInBufferView.setText(String.valueOf(monitor.getPointsInBuffer()));
		TextView gForcePointsInBufferView = (TextView) findViewById(R.id.GForcePointsInBuffer);
		gForcePointsInBufferView.setText(String.valueOf(monitor.getGforcePointsInBuffer()));
	}


	private class ResetMonitorListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			Monitor.getInstance().reset();
		}
	}
	
	private class RefreshMonitorListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			LegalTrackerActivity.this.updateUI();
		}
	}
}
