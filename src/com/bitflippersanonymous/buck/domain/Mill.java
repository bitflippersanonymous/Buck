package com.bitflippersanonymous.buck.domain;

import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;

public class Mill implements Util.DbItem {

	// Add more data fields here as needed.   Tags are used for db column creation
	public enum Tags { Name } 

	private int mId;
	private final HashMap<Tags, String> mData;

	public Mill(HashMap<Tags, String> data, int id) {
		mId = id;
		mData = data;
	}
		
	public int getId() {
		return mId;
	}
	
	public String get(Tags tag) {
		return mData.get(tag);
	}

	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Mills.name();
	}
	
	// Into DB
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		for ( Tags tag : Tags.values() ) {
			switch ( tag ) {
			default:
				values.put(tag.name(), get(tag));
				break;
			}
		}
		return values;
	}
	
	// Outof DB
	public static Mill cursorToItem(Cursor cursor) {
		if ( cursor == null || cursor.getCount() == 0 )
			return null;
		
		// Match column names in cursor to Tags and load HashMap data
		int id = cursor.getInt(0);
		HashMap<Tags, String> data = new HashMap<Tags, String>();
		String[] colNames = cursor.getColumnNames();
		for ( int i = 0; i< cursor.getColumnCount(); i++ ) {
			Tags tag = null;
			try { 
				tag = Tags.valueOf(colNames[i]);
				data.put(tag, cursor.getString(i));
			} catch(IllegalArgumentException ex) { }
		}
		return new Mill(data, id);
	}

}
