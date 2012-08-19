package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.ItemDbAdapter;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends BaseActivity 
	implements ActionBar.OnNavigationListener, MainListFragment.OnItemListener, 
	LoaderManager.LoaderCallbacks<Cursor>, ServiceConnection {

	private CursorAdapter mAdapter = null; // This cursor populates the mainlistfragment (ie, list of mills)
	
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
    	mAdapter = new ItemDbAdapter(this, null, 0);
    }

    @Override
    public void onResume() {
      super.onResume();
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
   
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given tab is selected, show the tab contents in the container
    	MainListFragment fragment = new MainListFragment();
        Bundle args = new Bundle();
        args.putInt(MainListFragment.ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        fragment.setOnItemListener(this); // May need to do this in onCreate and onResume
        
    	fragment.setListAdapter(mAdapter);
    	getSupportLoaderManager().restartLoader(0, null, this); // May still be waiting for service to load
		return true;
    }
    
    // Is called with you select a specific Mill/Job.  Will start a new activity to show details
	@Override
	public void onItemSelected(Object item) {
		Cursor cursor = (Cursor)item;
		switch ( getActionBar().getSelectedNavigationIndex() ) {
		case 0:
			Intent intent = new Intent(this, MillActivity.class);
			intent.putExtra(Util._ID, cursor.getInt(0));
			startActivity(intent);
			break;
		case 1:
			startActivity(new Intent(this, JobActivity.class));
			break;
		}
	}
	
	@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SimpleCursorLoader(this) {
			@Override
			public Cursor loadInBackground() {
				if ( !isBound() ) {
					
				}
				String table = Tables.Mills.name(); // FIXME: Get from args?
				BuckDatabaseAdapter db = getService().getDbAdapter();
				return db.fetchAll(table);
			}
		};
	}

	@Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
		
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
		getSupportLoaderManager().initLoader(0, null, this);
	}
}
