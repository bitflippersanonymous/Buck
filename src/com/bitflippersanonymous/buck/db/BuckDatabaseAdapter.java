package com.bitflippersanonymous.buck.db;

import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DbTags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class BuckDatabaseAdapter implements Util.DatabaseBase {

	private BuckDatabaseHelper mDbHelper = null;

	public BuckDatabaseAdapter(Context context) {
		mDbHelper = new BuckDatabaseHelper(context);
	}

	public BuckDatabaseAdapter recreate() throws SQLException {
		mDbHelper.onUpgrade(mDbHelper.getWritableDatabase(), 1, 1);
		return this;
	}

	public void close() {
		if ( mDbHelper != null )
		mDbHelper.close();
		mDbHelper = null;
	}
	
	public Cursor fetchAll(Tables table) throws SQLException {
		Cursor cursor = mDbHelper.getReadableDatabase().query(false, table.name(),
				null, null, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor fetchEntry(Tables table, long rowId) throws SQLException {
		Cursor cursor = mDbHelper.getReadableDatabase().query(true, table.name(), 
				null,
				Util._ID + "=?", new String[]{String.valueOf(rowId)}, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	/*
	public Cursor fetchItem(String table) throws SQLException {
		Cursor cursor = mDbHelper.getReadableDatabase().query(false, table,
				null, //columns
				null, //selection
				null, //selectionArgs
				null, //groupBy
				null, //having
				null, //sortby
				null);//limit
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	*/
	
	// Optionally could set id on item. (Would need to add setId to DbTags interface)
	public long insertItem(DbTags item) {
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		ContentValues values = item.getContentValues();
		return database.insert(item.getTableName(), null, values);
	}
	
}
