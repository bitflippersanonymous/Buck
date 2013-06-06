package com.bitflippersanonymous.buck.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bitflippersanonymous.buck.domain.Cut;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Price;
import com.bitflippersanonymous.buck.domain.Util;

/*
 * Could also use:
 * 		https://github.com/jgilfelt/android-sqlite-asset-helper
 * 			Save pre-created db as zipfile in assets. Copy to app on create. Built in support
 * 			for onUpgrade
 * 		https://github.com/greenrobot/greenDAO
 * 			Full blown Database Access Object system to replace partial DAO impl in Jobs, Mills, etc
 */

public class BuckDatabaseHelper extends SQLiteOpenHelper implements Util.DatabaseBase {

	private static final String DATABASE_CREATE = "database_create.sql";
	private static final String DATABASE_NAME = "applicationdata.db";
	private static final int DATABASE_VERSION = 1;
	private static BuckDatabaseHelper mSingleton = null;

	synchronized static BuckDatabaseHelper getInstance(Context context) {
		if ( mSingleton == null ) {
			mSingleton = new BuckDatabaseHelper(context.getApplicationContext());
		}
		return(mSingleton);
	}

	private final Context mAppContext;

	private BuckDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mAppContext = context.getApplicationContext();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			executeSqlScript(mAppContext, db, DATABASE_CREATE);
		} catch (IOException e) {
			Log.e(getClass().getName(), "Error executing sql file: " + DATABASE_CREATE);
		}
	}

	//https://github.com/greenrobot/greenDAO/blob/master/DaoCore/src/de/greenrobot/dao/DbUtils.java
	public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename)
			throws IOException {
		byte[] bytes = readAsset(context, assetFilename);
		String sql = new String(bytes, "UTF-8");
		String[] lines = sql.split(";(\\s)*[\n\r]");
		return executeSqlStatements(db, lines);
	}

	public static int executeSqlStatements(SQLiteDatabase db, String[] statements) {
		int count = 0;
		for (String line : statements) {
			line = line.trim();
			if (line.length() > 0) {
				db.execSQL(line);
				count++;
			}
		}
		return count;
	}

	public static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
		int byteCount = 0;
		byte[] buffer = new byte[4096];
		while (true) {
			int read = in.read(buffer);
			if (read == -1) {
				break;
			}
			out.write(buffer, 0, read);
			byteCount += read;
		}
		return byteCount;
	}

	public static byte[] readAllBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyAllBytes(in, out);
		return out.toByteArray();
	}

	public static byte[] readAsset(Context context, String filename) throws IOException {
		InputStream in = context.getResources().getAssets().open(filename);
		try {
			return readAllBytes(in);
		} finally {
			in.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(getClass().getName(),	"Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		onCreate(db);
	}

}
