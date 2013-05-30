package com.bitflippersanonymous.buck.ui;



import android.app.ActionBar;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.JobSummary;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.service.SimpleCursorLoader;

public class JobFragment extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Cursor>, Util.Update {

	private static final int JOB_LOADER = 0;
	private Job mJob = null;
	
	public JobFragment() {}
	
	@Override 
	public  void  onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
		//setListAdapter(new CursorAdapter(getActivity(), null));
		getLoaderManager().initLoader(JOB_LOADER, null, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_job, container, false);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Intent intent = new Intent(getActivity(), MeasureActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Loader<Cursor> loader; 
		final BuckDatabaseAdapter dB = BaseActivity.getService().getDbAdapter();
		loader = new SimpleCursorLoader(getActivity(), args) {
			@Override
			public Cursor loadInBackground() {
				switch ( getId() ) {
				case JOB_LOADER:
					return dB.fetchEntry(Tables.Jobs, getArguments().getInt(Util._ID));
				}
				return null;
			};
		};
		loader.forceLoad();
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch ( loader.getId() ) {
		case JOB_LOADER:
			mJob = new Job(cursor);
			String jobName = mJob.getAsString(Job.Fields.Name);
			ActionBar actionBar = getActivity().getActionBar();
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(jobName);
			break;
		}
		//getListAdapter().swapCursor(cursor);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch ( loader.getId() ) {
		case JOB_LOADER:
			mJob = null;
			break;
		}
	}
	
	@Override
	public void update() {
		getLoaderManager().restartLoader(JOB_LOADER, null, this);
	}
	
}