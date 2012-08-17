package com.bitflippersanonymous.buck;

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
		+ BuckDatabaseAdapter.Fields.RowId.name() + " integer primary key autoincrement, "
		+ Fields.Name.name() + " text not null" + ");",
		
		"create table " + Tables.Jobs.name() + " ("
		+ Fields.RowId.name() + " integer primary key autoincrement, "
		+ Fields.Name.name() + " text not null" + ");",
	};
	
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
