package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.JobDbAdapter;
import com.bitflippersanonymous.buck.domain.MillDbAdapter;
import com.bitflippersanonymous.buck.domain.SimpleCursorLoader;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.app.LoaderManager;
import android.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Toast;

public class MainActivity extends BaseActivity 
implements ActionBar.OnNavigationListener, MainListFragment.OnItemListener, 
LoaderManager.LoaderCallbacks<Cursor>, ServiceConnection {

	private CursorAdapter mAdapter = null; // This cursor populates the mainlistfragment (ie, list of mills)
	private static CursorAdapter[] mAdapters = {null, null};

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	//private static Class<?>[] mChildActivities = {MillActivity.class, JobActivity.class};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		final String[] navigation_items = getResources().getStringArray(R.array.navigation_items);
		actionBar.setListNavigationCallbacks(
				// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(
						actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1,
						navigation_items
						),
						this);
		mAdapters[0] = new MillDbAdapter(this, null, 0);
		mAdapters[1] = new JobDbAdapter(this, null, 0);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
				getActionBar().getSelectedNavigationIndex());
	}

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
		case R.id.menu_buck:
			Toast.makeText(this, "Buck", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.menu_settings:
			Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
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

		// Is a bit slow.  We requery each time
		(mAdapter = mAdapters[position]).swapCursor(null);
		fragment.setListAdapter(mAdapter);
		getLoaderManager().restartLoader(0, args, this);
		return true;
	}

	/**
	 *  Is called with you select a specific Mill/Job.  Will start a new activity to show details
	 *  @param item Item in the list that was selected. Since the ListAdapter is a cursor,
	 *  we get a cursor at the selected position.
	 *  @see MainListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(Object item) {
		if (!( item instanceof Cursor) ) {
			Log.e(getClass().getSimpleName(), "onItemSelected: item is not a Cursor");
		}
		Class<?> fragmentClass = MillFragment.class;
		if ( getActionBar().getSelectedNavigationIndex() == 1 )
			fragmentClass = JobFragment.class;
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
				String table = Tables.Mills.name();
				Bundle args = getArgs();
				if ( args != null && args.getInt(MainListFragment.ARG_SECTION_NUMBER) == 1)
					table = Tables.Jobs.name();
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
	 * Delay initLoader until we have a service connection.
	 * initLoader runs an async db query to load main ListView. (list of mills/jobs)
	 * @see BaseActivity#onServiceConnected(android.content.ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
		getLoaderManager().initLoader(0, null, this);
	}
}
