package com.bitflippersanonymous.buck.db;

import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class BuckDatabaseHelper extends SQLiteOpenHelper implements Util.DatabaseBase {

	private static final String DATABASE_NAME = "applicationdata.db";
	private static final int DATABASE_VERSION = 1;

	// Strings to recreate DB from scratch
	private static final String [] CREATE_TABLES = { 
		"create table " + Tables.Mills.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",
		
		"create table " + Tables.Jobs.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",

	};
	
	static {
		for ( String tag : Mill.getTags() ) {
			CREATE_TABLES[0] += ", " + tag + " text "; 
		}
		CREATE_TABLES[0] += ");";
		
		for ( String tag : Job.getTags() ) {
			CREATE_TABLES[1] += ", " + tag + " text "; 
		}
		
		CREATE_TABLES[1] += ");";

	}
	
	public BuckDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for ( String table : CREATE_TABLES )
			db.execSQL(table);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(getClass().getName(),	"Upgrading database from version " + oldVersion + " to "
			+ newVersion + ", which will destroy all old data");
		for ( Tables table : Tables.values() )
			db.execSQL("DROP TABLE IF EXISTS " + table.name());
		onCreate(db);
	}

}
