package com.javaapps.legaltracker.activity;

import java.io.FileNotFoundException;
import java.io.IOException;


import com.javaapps.legaltracker.R;
import com.javaapps.legaltracker.R.id;
import com.javaapps.legaltracker.R.layout;
import com.javaapps.legaltracker.R.menu;
import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.io.LegalTrackerFileFactory;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConfigurationActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		Button saveConfigurationButton = (Button) this
				.findViewById(R.id.saveConfigurationButton);
		saveConfigurationButton.setOnClickListener(new ConfigurationListener());
		Button cleanArchivesButton = (Button) this
				.findViewById(R.id.cleanArchiveFiles);
		cleanArchivesButton.setOnClickListener(new CleanArchivesListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.javaapps.legaltracker.R.menu.main, menu);
		EditText serverURLEntry = (EditText) this
				.findViewById(R.id.serverURLEntry);
		serverURLEntry.setText(Config.getInstance().getLocationDataEndpoint());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.monitorLegalTracker:
			Intent configurationIntent = new Intent(this,
					LegalTrackerActivity.class);
			startActivity(configurationIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class CleanArchivesListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			LegalTrackerFile legalTrackerFile;
			try {
				legalTrackerFile = LegalTrackerFileFactory.getLegalTrackerFile(
						FileType.Location.getPrefix(),
						FileType.Location.getExtension());
				legalTrackerFile.deleteFiles();
			} catch (Exception e) {
				Log.e(Constants.LEGAL_TRACKER_TAG, "Could not delete archive files because "
						+ e.getMessage());
			}
		}
	}

	private class ConfigurationListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			try
			{
			EditText serverURLEntry = (EditText) ConfigurationActivity.this
					.findViewById(R.id.serverURLEntry);
			Config.getInstance().setLocationDataEndpoint(
					serverURLEntry.getText().toString());
			EditText testStatusCodeEntry = (EditText) ConfigurationActivity.this
					.findViewById(R.id.TestStatusCode);
			Config.getInstance().setTestStatusCode(
					Integer.parseInt(testStatusCodeEntry.getText().toString()));
			}catch(Exception ex){
				//just to prevent a int parse exception
			}
		}

	}
}
