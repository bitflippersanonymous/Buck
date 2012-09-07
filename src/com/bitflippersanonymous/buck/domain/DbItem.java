package com.bitflippersanonymous.buck.domain;

import java.util.AbstractMap;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class DbItem implements Util.DbTags {

	private final Tags[] mTags;
	private int mId;
	private final HashMap<String, String> mData;

	public Tags[] getTags() {
		return mTags;
	}
	
	protected DbItem(Tags[] tags, Cursor cursor) {
		mTags = tags;
		mData = new HashMap<String, String>();
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

	public DbItem(Tags[] tags, HashMap<String, String> data, int id) {
		mTags = tags;
		mData = data;
		mId = id;
	}

	public int getId() {
		return mId;
	}
	
	public String get(String tag) {
		return mData.get(tag);
	}
	
	// Into DB
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		for ( Tags tag : getTags() ) {
			values.put(tag.getKey(), get(tag.getKey()));
		}
		return values;
	}
	
	public abstract String getTableName();
}
