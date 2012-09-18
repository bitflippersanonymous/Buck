package com.bitflippersanonymous.buck.ui;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Util;

public class DetailActivity extends BaseActivity {
	private Fragment mFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		Class<?> fragmentClass = (Class<?>) getIntent().getSerializableExtra(Util.FRAGMENT);
		mFragment = null;
		try {
			mFragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Cannot create fragment class: " + fragmentClass.getSimpleName() );
			return;
		}
		
		// FIXME: actionBar title needs set to mill/job name here with a common interface
		
		// Pass along all the extras.  Could create a new bundle and only pass ones we need
		mFragment.setArguments(getIntent().getExtras());
		
		getFragmentManager().beginTransaction()
		.replace(R.id.detail_container, mFragment)
		.commit();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mFragment = null;
	}
	
	@Override
	public void update() {
		if ( mFragment instanceof Util.Update )
			((Util.Update)mFragment).update();
	}
}
