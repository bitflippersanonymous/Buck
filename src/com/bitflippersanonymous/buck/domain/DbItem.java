package com.bitflippersanonymous.buck.domain;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class DbItem implements Util.DbTags {

	private final Tag[] mTags;
	private int mId;
	private final ContentValues mData;

	public Tag[] getTags() {
		return mTags;
	}
	
	// Out of DB
	protected DbItem(Tag[] tags, Cursor cursor) {
		mTags = tags;
		mData = new ContentValues();
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

	protected DbItem(Tag[] tags, int id) {
		mTags = tags;
		mData = new ContentValues();
		mId = id;
	}

	public int getId() {
		return mId;
	}

	//TODO: Check that Tag matches type
	public String getAsString(Enum<?> field) {
		return mData.getAsString(field.name());
	}

	public Integer getAsInteger(Enum<?> field) {
		return mData.getAsInteger(field.name());
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
