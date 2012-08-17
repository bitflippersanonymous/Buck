package com.bitflippersanonymous.buck.domain;

import android.content.ContentValues;

public class Util {
	public static final String _ID = "_id"; 
	
	// Define an common interface for db classes for shared strings
	public interface DatabaseBase {
		public enum Tables {Mills, Jobs}
	}
	
	public interface DbItem {
		ContentValues createContentValues();
		String getTableName();
	}

}
