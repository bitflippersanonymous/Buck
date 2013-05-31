package com.bitflippersanonymous.buck.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;

import android.content.ContentValues;

public class Util {
	public static final String _ID = "_id";
	public static final String FRAGMENT = "FRAGMENT";
	public static final String CUTS = "CUTS";
	public static final String SCRIBNER = "scribner.csv"; 
	public static final String TABLE = "TABLE";
	public static final String MILL = "MILL";
	public static final String JOB = "JOB";
	
	// Define an common interface for db classes for shared strings
	public interface DatabaseBase {
		public enum Tables {Mills, Jobs, Prices, Cuts}
	}

	public interface InsertItems {
		public long insertMill(Mill mill);
	}

	public interface Update {
		void update();
	}
	
	public interface HasFields {
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
		void handleLine(String line);
		InputStream getInputStream() throws IOException;
		String getFilename();
	}

}
