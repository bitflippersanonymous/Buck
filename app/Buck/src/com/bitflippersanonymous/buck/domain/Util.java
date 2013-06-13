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
		public enum Tables { Mills, Jobs, Prices, Cuts };
		public enum Views { Job_Totals };
	}

	public interface InsertItems {
		public long insertMill(Mill mill);
	}

	public interface Update {
		void update();
	}
		
	public interface FileReader {
		void handleLine(String line);
		InputStream getInputStream() throws IOException;
		String getFilename();
	}

	public interface DbContent {
		ContentValues getContentValues();
		String getTableName();
	}

	
}
