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

	public interface InsertItems {
		public long insertMill(Mill mill);
	}

	interface HasFields {
		enum Fields {};
	}
	
	public interface DbTags {
		enum DataType {text, integer}
		class Tag extends AbstractMap.SimpleEntry<String, DataType> {
			private static final long serialVersionUID = 1L;
			public Tag(Enum<?> e, DataType dataType) {
				super(e.name(), dataType);
			}
			public Tag(Enum<?> e) {
				super(e.name(), DataType.text);
			}	
		}
		ContentValues getContentValues();
		String getTableName();
	}
	
	public interface FileReader {
		public String getFilename();
		public void handleLine(String line);
	}

}
