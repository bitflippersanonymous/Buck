package com.bitflippersanonymous.buck;

import android.content.ContentValues;

public class Util {
	public static final String ID = "ID"; 
	
	// Define an common interface for db classes for shared strings
	public interface DatabaseBase {
		public enum Tables {Mills, Jobs}
		public enum Fields {RowId, Name}
	}

	public interface DbItem {
		ContentValues createContentValues();
		String getTableName();
	}
}
