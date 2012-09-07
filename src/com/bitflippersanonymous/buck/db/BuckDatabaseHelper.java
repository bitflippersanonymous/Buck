package com.bitflippersanonymous.buck.db;

import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Price;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DbTags;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class BuckDatabaseHelper extends SQLiteOpenHelper implements Util.DatabaseBase {

	private static final String DATABASE_NAME = "applicationdata.db";
	private static final int DATABASE_VERSION = 1;

	// Strings to recreate DB from scratch
	// FIXME: refactor with a loop. duplicate code
	private static final String [] CREATE_TABLES = { 
		"create table " + Tables.Mills.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",
		
		"create table " + Tables.Jobs.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",

		"create table " + Tables.Prices.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",

	};
	
	static {
		for ( DbTags.Tags column : Mill.getsTags() ) {
			CREATE_TABLES[0] += ", " + column.getKey() + " " + column.getValue(); 
		}
		CREATE_TABLES[0] += ");";
		
		
		for ( DbTags.Tags column : Job.getsTags() ) {
			CREATE_TABLES[1] += ", " + column.getKey() + " " + column.getValue(); 
		}
		CREATE_TABLES[1] += ");";

		for ( DbTags.Tags column : Price.getsTags() ) {
			CREATE_TABLES[2] += ", " + column.getKey() + " " + column.getValue(); 
		}
		CREATE_TABLES[2] += ");";
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
