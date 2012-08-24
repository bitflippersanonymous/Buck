package com.bitflippersanonymous.buck.domain;

import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;

public class Job implements Util.DbItem {

	// Add more data fields here as needed.   Tags are used for db column creation
	//public enum Tags { Name } 
	
	private static final String[] sTags = {"Name", "Location"};
	
	public static	String[] getTags() {
		return sTags;
	}

	private int mId;
	private final HashMap<String, String> mData;

	public Job(HashMap<String, String> data, int id) {
		mId = id;
		mData = data;
	}
		
	public int getId() {
		return mId;
	}
	
	public String get(String tag) {
		return mData.get(tag);
	}

	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Jobs.name();
	}
	
	// Into DB
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		for ( String tag : getTags() ) {
				values.put(tag, get(tag));
			}
		
		return values;
	}
	
	// Outof DB
	public static Job cursorToItem(Cursor cursor) {
		if ( cursor == null || cursor.getCount() == 0 )
			return null;
		
		// Match column names in cursor to Tags and load HashMap data
		int id = cursor.getInt(0);
		HashMap<String, String> data = new HashMap<String, String>();
		String[] colNames = cursor.getColumnNames();
		for ( int i = 0; i< cursor.getColumnCount(); i++ ) {
			String tag = colNames[i];
			data.put(tag, cursor.getString(i));
		}
		return new Job(data, id);
	}
}
