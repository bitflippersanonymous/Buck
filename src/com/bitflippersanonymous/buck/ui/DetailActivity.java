package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Util;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

public class DetailActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		Class<?> fragmentClass = (Class<?>) getIntent().getSerializableExtra(Util.FRAGMENT);
		Fragment fragment = null;
		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Cannot create fragment class: " + fragmentClass.getSimpleName() );
			return;
		}
		
		// FIXME: actionBar title needs set to mill/job name here with a common interface
		
		// Pass along all the extras.  Could create a new bundle and only pass ones we need
		fragment.setArguments(getIntent().getExtras());
		
		getFragmentManager().beginTransaction()
		.replace(R.id.detail_container, fragment)
		.commit();

	}

	/**
	 * Handles option item selections, including 'Home', which restarts the main activity
	 * resetting your current location to default Mills. Buck needs a home landing activity
	 * instead
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent.addFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TOP |
					Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
