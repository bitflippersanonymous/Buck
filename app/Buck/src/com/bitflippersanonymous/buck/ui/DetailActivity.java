package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Util;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;

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
}