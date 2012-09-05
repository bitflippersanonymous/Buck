package com.bitflippersanonymous.buck.ui;

import java.util.ArrayList;
import java.util.List;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Cut;
import com.bitflippersanonymous.buck.domain.CutPlan;
import com.bitflippersanonymous.buck.domain.PickerAdapter;
import com.bitflippersanonymous.buck.domain.Util;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;



public class PickerActivity extends BaseActivity 
	implements LoaderManager.LoaderCallbacks<List<CutPlan>>,
		OnItemClickListener {

	private PickerAdapter mAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picker);
		
		// TODO: This code is common to all BaseActivity derived classes... move it up
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_activity_picker);
		
		ListView list = (ListView) findViewById(R.id.listViewPicker);
		list.setAdapter(mAdapter = new PickerAdapter(this, 0, new ArrayList<CutPlan>()));
		list.setOnItemClickListener(this);
		
		findViewById(R.id.listViewPicker).setVisibility(View.GONE);
		findViewById(R.id.progressBarPicker).setVisibility(View.VISIBLE);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<CutPlan>> onCreateLoader(int id, Bundle args) {
		Loader<List<CutPlan>> loader = new AsyncTaskLoader<List<CutPlan>>(this) {
			@Override
			public List<CutPlan> loadInBackground() {
                ArrayList<Cut> cuts = getIntent().getParcelableArrayListExtra(Util.CUTS);
				return getService().getCutPlans(cuts);
			}
		};
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<CutPlan>> loader, List<CutPlan> cutPlans) {
		mAdapter.addAll(cutPlans);
		mAdapter.notifyDataSetChanged();
		findViewById(R.id.listViewPicker).setVisibility(View.VISIBLE);
		findViewById(R.id.progressBarPicker).setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<List<CutPlan>> loader) {
		mAdapter.clear();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Intent intent = new Intent(this, BuckActivity.class);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getService().savePick(mAdapter.getItem(position));
		finish();
	}
}
