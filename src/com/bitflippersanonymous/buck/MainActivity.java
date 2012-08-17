package com.bitflippersanonymous.buck;

import com.bitflippersanonymous.buck.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MainActivity extends FragmentActivity 
	implements ActionBar.OnNavigationListener, MainListFragment.OnItemListener  {

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
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given tab is selected, show the tab contents in the container
    	MainListFragment fragment = new MainListFragment();
        fragment.setListAdapter(getItemListAdapter(position));
        Bundle args = new Bundle();
        args.putInt(MainListFragment.ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        fragment.setOnItemListener(this); // May need to do this in onCreate and onResume
        return true;
    }
    
    private ListAdapter getItemListAdapter(int position) {
    	int index = 0;
    	switch ( position ) {
    	case 0: // FIXME: wish I could do better than 0, 1. Java enums are wack 
    		index = R.array.mills;
    		break;
    	case 1:
    		index = R.array.jobs;
    		break;
    	default:
    	}

    	final String[] items = getResources().getStringArray(index);

    	ListAdapter adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                items);
		return adapter;
    }

    // Is called with you select a specific Mill/Job.  Will start a new activity to show details
	@Override
	public void onItemSelected(Object item) {
		switch ( getActionBar().getSelectedNavigationIndex() ) {
		case 0:
			startActivity(new Intent(this, MillActivity.class));
			break;
		case 1:
			startActivity(new Intent(this, JobActivity.class));
			break;
		}
	}
}
