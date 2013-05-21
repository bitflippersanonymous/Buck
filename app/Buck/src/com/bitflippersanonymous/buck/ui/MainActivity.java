package com.bitflippersanonymous.buck.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Toast;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.JobDbAdapter;
import com.bitflippersanonymous.buck.domain.MillDbAdapter;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.service.SimpleCursorLoader;

public class MainActivity extends BaseActivity 
implements ActionBar.OnNavigationListener, MainListFragment.OnItemListener, 
LoaderManager.LoaderCallbacks<Cursor>, ServiceConnection {

	private static final int MILL_IDX = 0;
	private static final int JOB_IDX = 1;
	
	private CursorAdapter mAdapter = null; // This cursor populates the mainlistfragment (ie, list of mills)
	private static CursorAdapter[] mAdapters = {null, null};

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	//private static Class<?>[] mChildActivities = {MillActivity.class, JobActivity.class};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Clear prefs
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.clear().apply();
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		final String[] navigation_items = getResources().getStringArray(R.array.navigation_items);
		actionBar.setListNavigationCallbacks(
				// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(
						getBaseContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1,
						navigation_items
						),
						this);
		
		mAdapters[MILL_IDX] = new MillDbAdapter(this, null, 0); 
		mAdapters[JOB_IDX] = new JobDbAdapter(this, null, 0);

		//Possibly .unregisterListener(listener) to prevent callback before have service
		//How is this the correct idx?
		int idx = getActionBar().getSelectedNavigationIndex();
		getLoaderManager().initLoader(idx, null, this);
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	// Since this activity is first in the stack, when it's destroyed the service 
	// is destroyed too.  Could fix that by binding to BuckService in a derived
	// Application class
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			int idx = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
			getActionBar().setSelectedNavigationItem(idx);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
				getActionBar().getSelectedNavigationIndex());
	}

	@Override
	protected void update() {
		super.update();
		// Should update all of them?
		int idx = getActionBar().getSelectedNavigationIndex();
		getLoaderManager().restartLoader(idx, null, this);
	}
	
	//FIXME: I wonder if I can call through to super and append option menus
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_measure:
			Intent intent = new Intent(this, MeasureActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called when switching view between Mills/Jobs.  Restarts Loader to re-query db.
	 * Create a new bundle with the position args to tell the MainListFragment what we're
	 * showing, though it's currently unused.
	 */
	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given tab is selected, show the tab contents in the container
		MainListFragment fragment = new MainListFragment();
		Bundle args = new Bundle();
		args.putInt(MainListFragment.ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		getFragmentManager().beginTransaction()
		.replace(R.id.container, fragment)
		.commit();
		fragment.setOnItemListener(this); // May need to do this in onCreate and onResume

		(mAdapter = mAdapters[position]).swapCursor(null);
		fragment.setListAdapter(mAdapter);
		getLoaderManager().initLoader(position, args, this); 
		//Possibly .unregisterListener(listener) to prevent callback before have service
		return true;
	}

	/**
	 *  Is called with you select a specific Mill/Job.  Will start a new activity to show details
	 *  @param item Item in the list that was selected. Since the ListAdapter is a cursor,
	 *  we get a cursor at the selected position.
	 *  @see MainListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(Object item, View view) {
		if (!( item instanceof Cursor) ) {
			Log.e(getClass().getSimpleName(), "onItemSelected: item is not a Cursor");
		}
		Class<?> fragmentClass = null;
		if ( getActionBar().getSelectedNavigationIndex() == JOB_IDX )
			fragmentClass = JobFragment.class;
		else
			fragmentClass = MillFragment.class;
		Cursor cursor = (Cursor)item;
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra(Util.FRAGMENT, fragmentClass);
		intent.putExtra(Util._ID, cursor.getInt(0));
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SimpleCursorLoader(this, args) {
			@Override
			public Cursor loadInBackground() {
				Tables table = Tables.Mills;
				Bundle args = getArgs();
				if ( args != null && args.getInt(MainListFragment.ARG_SECTION_NUMBER) == JOB_IDX )
					table = Tables.Jobs;
				BuckDatabaseAdapter db = getService().getDbAdapter();
				return db.fetchAll(table);
			}
		};
	}

	/**
	 * Called when loader async task has finished and cursor has a new data for the adapter 
	 * mAdapter is null on return to activity where onServiceConnected is called as activity is unpaused.  
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if ( mAdapter != null )
			mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	/**
	 * Do things now that we have a connection to the service
	 * @see BaseActivity#onServiceConnected(android.content.ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
	}
}
