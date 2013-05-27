package com.bitflippersanonymous.buck.service;

import java.util.List;

import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;

public class CursorLoaderAdapter implements LoaderManager.LoaderCallbacks<Cursor> {

	private Context mContext;
	private List<CursorAdapter> mAdapters;
	private BuckDatabaseAdapter mDb;
	
	public CursorLoaderAdapter(Context context, List<CursorAdapter> adapters, 
			BuckDatabaseAdapter db) {
		mContext = context;
		mAdapters = adapters;
		mDb = db;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SimpleCursorLoader(mContext, args) {
			@Override
			public Cursor loadInBackground() {
				Tables table = (Tables)getArgs().getSerializable(Util.TABLE);
				return mDb.fetchAll(table);
			}
		};
	}

	/**
	 * Called when loader async task has finished and cursor has a new data for the adapter 
	 * mAdapter is null on return to activity where onServiceConnected is called as activity is unpaused.  
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int id = loader.getId();
		if ( mAdapters.get(id) != null )
			mAdapters.get(id).swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapters.get(loader.getId()).swapCursor(null);
	}

	
}
