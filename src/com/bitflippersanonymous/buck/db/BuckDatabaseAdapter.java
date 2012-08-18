package com.bitflippersanonymous.buck.db;

import com.bitflippersanonymous.buck.domain.Util;
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
	
	public Cursor fetchAll(String table) throws SQLException {
		Cursor cursor = mDbHelper.getReadableDatabase().query(false, table,
				null, null, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public long insertItem(Util.DbItem item) {
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		ContentValues values = item.createContentValues();
		return database.insert(item.getTableName(), null, values);
	}
	
}
