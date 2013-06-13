package com.bitflippersanonymous.buck.domain;

import com.bitflippersanonymous.buck.domain.Util.DbContent;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class DbItem implements DbContent {

	private int mId;
	private final ContentValues mData = new ContentValues();
	
	// Out of DB
	protected DbItem(Cursor cursor) {
		if ( cursor == null || cursor.getCount() == 0 )
			return;
		
		// FIXME Match column names to tag keys
		mId = cursor.getInt(0);
		String[] colNames = cursor.getColumnNames();
		for ( int i = 0; i< cursor.getColumnCount(); i++ ) {
			String tag = colNames[i];
			mData.put(tag, cursor.getString(i));
		}
	}

	protected DbItem() {
		mId = -1;
	}

	public int getId() {
		return mId;
	}
	
	public void setId(int id) {
		mId = id;
	}

	public String getAsString(Enum<?> field) {
		return mData.getAsString(field.name());
	}

	public Integer getAsInteger(Enum<?> field) {
		return mData.getAsInteger(field.name());
	}
	
	public void put(String field, String value) {
		mData.put(field, value);
	}
	
	public void put(Enum<?> field, String value) {
		mData.put(field.name(), value);
	}
	
	public void put(Enum<?> field, Integer value) {
		mData.put(field.name(), value);
	}
	//
	
	// Into DB
	public ContentValues getContentValues() {
		return mData;
	}
	
	public abstract String getTableName();

}
