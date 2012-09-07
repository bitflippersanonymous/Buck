package com.bitflippersanonymous.buck.domain;

import java.util.AbstractMap;

import android.content.ContentValues;

public class Util {
	public static final String _ID = "_id";
	public static final String FRAGMENT = "FRAGMENT";
	public static final String CUTS = "CUTS"; 
	
	// Define an common interface for db classes for shared strings
	public interface DatabaseBase {
		public enum Tables {Mills, Jobs, Prices}
	}
	
	public interface DbTags {
		enum DataType {text, integer}
		class Tags extends AbstractMap.SimpleEntry<String, DataType> {
			public Tags(String string, DataType dataType) {
				super(string, dataType);
			}
			public Tags(String string) {
				super(string, DataType.text);
			}	
		}
		ContentValues createContentValues();
		String getTableName();
	}
	
	public interface FileReader {
		public String getFilename();
		public void handleLine(String line);
	}

}
