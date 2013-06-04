package com.bitflippersanonymous.buck.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.CutsAdapter;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.service.BuckService;
import com.bitflippersanonymous.buck.service.SimpleCursorLoader;

public class JobFragment extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Cursor>, Util.Update {

	private static final int LOADER_JOB = 0;
	private static final int LOADER_CUTS = 1;
	
	private Job mJob = null;
	private CutsAdapter mCutsAdapter;
	
	public JobFragment() {}
	
	@Override 
	public  void  onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
		getLoaderManager().initLoader(LOADER_JOB, null, this);
		getLoaderManager().initLoader(LOADER_CUTS, null, this);
		setListAdapter(mCutsAdapter = new CutsAdapter(null));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_job, container, false);
		return view;
	}

	/*
	private void populateHeader(View view) {
		final Resources res = getResources();
		((TextView)view.findViewById(R.id.textViewCutCuts)).setText(res.getString(R.string.cutheader_cuts));
		((TextView)view.findViewById(R.id.textViewCutBf)).setText(res.getString(R.string.cutheader_bf));
		((TextView)view.findViewById(R.id.textViewCutValue)).setText(res.getString(R.string.cutheader_value));
	}
	*/
	
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
			intent.putExtra(Util.JOB, mJob.getId());
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Loader<Cursor> loader; 
		loader = new SimpleCursorLoader(getActivity(), args) {
			@Override
			public Cursor loadInBackground() {
				BuckService service = BaseActivity.getService();
				switch ( getId() ) {
				case LOADER_JOB:
					return service.getItem(Tables.Jobs, getArguments().getInt(Util._ID));
				case LOADER_CUTS:
					return service.getCuts(getArguments().getInt(Util._ID));
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
		case LOADER_JOB:
			mJob = new Job(cursor);
			String jobName = mJob.getAsString(Job.Fields.Name);
			ActionBar actionBar = getActivity().getActionBar();
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(jobName);
			break;
		case LOADER_CUTS:
			mCutsAdapter.swapCursor(cursor);
			break;
		}
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch ( loader.getId() ) {
		case LOADER_JOB:
			mJob = null;
			break;
		case LOADER_CUTS:
			mCutsAdapter.swapCursor(null);
			break;
		}
	}
	
	@Override
	public void update() {
		getLoaderManager().restartLoader(LOADER_JOB, null, this);
		getLoaderManager().restartLoader(LOADER_CUTS, null, this);
	}
	
}