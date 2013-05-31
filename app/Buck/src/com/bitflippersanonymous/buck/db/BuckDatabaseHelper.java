package com.bitflippersanonymous.buck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bitflippersanonymous.buck.domain.Cut;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Price;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DbTags;


public class BuckDatabaseHelper extends SQLiteOpenHelper implements Util.DatabaseBase {

	private static final String DATABASE_NAME = "applicationdata.db";
	private static final int DATABASE_VERSION = 1;
	private static BuckDatabaseHelper mSingleton = null;
	
	synchronized static BuckDatabaseHelper getInstance(Context context) {
		if ( mSingleton == null ) {
			mSingleton = new BuckDatabaseHelper(context.getApplicationContext());
		}
		return(mSingleton);
	}
	
	private BuckDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Strings to recreate DB from scratch
	// FIXME: refactor with a loop. duplicate code
	private static final String [] CREATE_TABLES = { 
		"create table " + Tables.Mills.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",
		
		"create table " + Tables.Jobs.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",

		"create table " + Tables.Prices.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",

		"create table " + Tables.Cuts.name() + " ("
		+ Util._ID + " integer primary key autoincrement ",
	};
	
	static {
		int i = 0;
		for ( DbTags.Tag column : Mill.getsTags() ) {
			CREATE_TABLES[i] += ", " + column.getKey() + " " + column.getValue(); 
		}
		CREATE_TABLES[i] += ");";
		
		i++;
		for ( DbTags.Tag column : Job.getsTags() ) {
			CREATE_TABLES[i] += ", " + column.getKey() + " " + column.getValue(); 
		}
		CREATE_TABLES[i] += ");";
		
		i++;
		for ( DbTags.Tag column : Price.getsTags() ) {
			CREATE_TABLES[i] += ", " + column.getKey() + " " + column.getValue(); 
		}
		CREATE_TABLES[i] += ");";
		
		i++;
		for ( DbTags.Tag column : Cut.getsTags() ) {
			CREATE_TABLES[i] += ", " + column.getKey() + " " + column.getValue(); 
		}
		CREATE_TABLES[i] += ");";

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
