package com.bitflippersanonymous.buck;

import java.sql.Time;
import java.util.HashMap;


import android.content.ContentValues;
import android.database.Cursor;

public class Mill implements Util.DbItem {

	@Override
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		return values;
	}
	
	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Mills.name();
	}

	/*
	// Into DB
	@Override
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		for ( Tags tag : Tags.values() ) {
			switch ( tag ) {
			case keywords:
				continue;
			case enqueue:
				values.put(tag.name(), false);
				break;
			case pubDate:
				values.put(tag.name(), Time.parse(get(tag)));
				break;
			default:
				values.put(tag.name(), get(tag));
				break;
			}
		}
		return values;
	}
	
	// Outof DB
	public static PlsEntry cursorToEntry(Cursor cursor) {
		if ( cursor == null || cursor.getCount() == 0 )
			return null;
		
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
		return new PlsEntry(data, id);
	}
*/
}
