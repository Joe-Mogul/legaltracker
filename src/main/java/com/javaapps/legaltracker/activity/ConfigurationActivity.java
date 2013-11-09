package com.javaapps.legaltracker.activity;

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

import com.javaapps.legaltracker.R;
import com.javaapps.legaltracker.db.LegalTrackerDBAdapter;
import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;

public class ConfigurationActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		try
		{
		LegalTrackerDBAdapter dbAdapter = new LegalTrackerDBAdapter(this);
		dbAdapter.open();
		Config.getInstance().setCustomIdentifier(dbAdapter.getValue(Constants.CUSTOM_IDENTIFIER));
		dbAdapter.close();
		EditText customIdenfierEntry = (EditText) this
				.findViewById(R.id.customIdentifier);
		customIdenfierEntry.setText(Config.getInstance().getCustomIdentifier());
		}catch(Exception ex){
			Log.e(Constants.LEGAL_TRACKER_TAG,"Cannot retrieve custom identifier because "+ex.getMessage());
		}
		Button saveConfigurationButton = (Button) this
				.findViewById(R.id.saveConfigurationButton);
		saveConfigurationButton.setOnClickListener(new ConfigurationListener());
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
				legalTrackerFile = new LegalTrackerFile(
						FileType.Location.getPrefix(),
						FileType.Location.getExtension());
				legalTrackerFile.deleteFiles();
			} catch (Exception e) {
				Log.e(Constants.LEGAL_TRACKER_TAG,
						"Could not delete archive files because "
								+ e.getMessage());
			}
		}
	}

	private class ConfigurationListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			try {
				EditText customIdenfierEntry = (EditText) ConfigurationActivity.this
						.findViewById(R.id.customIdentifier);
				String customIdentifierValue = customIdenfierEntry.getText()
						.toString();
				if (customIdentifierValue != null
						&& customIdentifierValue.trim().length() > 0) {
					LegalTrackerDBAdapter dbAdapter = new LegalTrackerDBAdapter(
							ConfigurationActivity.this);
					dbAdapter.open();
					dbAdapter.insertValue(Constants.CUSTOM_IDENTIFIER,
							customIdentifierValue);
					Config.getInstance().setCustomIdentifier(dbAdapter.getValue(customIdentifierValue));
					dbAdapter.close();
				}
			} catch (Exception ex) {
				// just to prevent a int parse exception
			}
		}

	}
}
