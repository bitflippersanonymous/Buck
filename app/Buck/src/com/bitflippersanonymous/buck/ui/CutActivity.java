package com.bitflippersanonymous.buck.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.CutAdapter;
import com.bitflippersanonymous.buck.domain.CutNode;
import com.bitflippersanonymous.buck.domain.Dimension;
import com.bitflippersanonymous.buck.domain.Util;



public class CutActivity extends BaseActivity 
	implements LoaderManager.LoaderCallbacks<List<CutNode>>,
		OnItemClickListener {

	private CutAdapter mAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cut);
		
		// TODO: This code is common to all BaseActivity derived classes... move it up
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_activity_cut);
		
		ListView list = (ListView) findViewById(R.id.listViewCut);
		list.setAdapter(mAdapter = new CutAdapter(this, 0, new ArrayList<CutNode>()));
		list.setOnItemClickListener(this);
		
		findViewById(R.id.listViewCut).setVisibility(View.GONE);
		findViewById(R.id.progressBarCut).setVisibility(View.VISIBLE);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<CutNode>> onCreateLoader(int id, Bundle args) {
		Loader<List<CutNode>> loader = new AsyncTaskLoader<List<CutNode>>(this) {
			@Override
			public List<CutNode> loadInBackground() {
                ArrayList<Dimension> cuts = getIntent().getParcelableArrayListExtra(Util.CUTS);
				return getService().getCutPlans(cuts);
			}
		};
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<CutNode>> loader, List<CutNode> cutPlans) {
		mAdapter.addAll(cutPlans); //@@@ Makes copy of array
		mAdapter.notifyDataSetChanged();
		findViewById(R.id.listViewCut).setVisibility(View.VISIBLE);
		findViewById(R.id.progressBarCut).setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<List<CutNode>> loader) {
		mAdapter.clear();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Intent intent = new Intent(this, MeasureActivity.class);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getService().savePick(mAdapter.getItem(position));
		finish();
	}
}
