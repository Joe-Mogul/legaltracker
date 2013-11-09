package com.javaapps.legaltracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.javaapps.legaltracker.pojos.Constants;

public class LegalTrackerDBAdapter {

	private DatabaseHelper legalTrackerDBHelper;
	private SQLiteDatabase legalTrackerDB;

	private final Context context;

	private final static String DATABASE_CREATE = "create table "
			+ Constants.DATABASE_TABLE + " ("
			+ "KEY text not null, Value text not null)";

	public LegalTrackerDBAdapter(Context context) {
		this.context = context;
	};

	public int deleteValue(String key) {
		return legalTrackerDB.delete(Constants.DATABASE_TABLE, "KEY=?",
				new String[] { key });
	}

	public String getValue(String key) {
		Cursor cursor = legalTrackerDB.query(Constants.DATABASE_TABLE,
				new String[] { "VALUE" }, "KEY=?", new String[] { key }, null,
				null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			return cursor.getString(0);
		} else {
			return null;
		}
	}

	public long insertValue(String key, String value) {
		deleteValue(key);
		long retValue=-1;
		try
		{
		ContentValues contentValues = new ContentValues();
		contentValues.put("KEY", key);
		contentValues.put("VALUE", value);
		retValue=legalTrackerDB.insert(Constants.DATABASE_TABLE, null,
				contentValues);
		}
		catch(Exception ex){
			Log.e(Constants.LEGAL_TRACKER_TAG,"Could not insert into database because "+ex.getMessage());
		}
		return retValue;
	}

	public LegalTrackerDBAdapter open() throws SQLException {
		legalTrackerDBHelper = new DatabaseHelper(context);
		legalTrackerDB = legalTrackerDBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		legalTrackerDBHelper.close();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, Constants.DATABASE_NAME, null,
					Constants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// not used yet
		}

	}

}
