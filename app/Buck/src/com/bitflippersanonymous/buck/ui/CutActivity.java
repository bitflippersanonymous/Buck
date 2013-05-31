package com.bitflippersanonymous.buck.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.CutAdapter;
import com.bitflippersanonymous.buck.domain.CutNode;
import com.bitflippersanonymous.buck.domain.Dimension;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.service.SimpleCursorLoader;

public class CutActivity extends BaseActivity 
	implements LoaderManager.LoaderCallbacks<List<CutNode>>,
		OnItemClickListener, OnItemSelectedListener {

	enum ViewState { LOADING, LOADED };
	enum Loaders { LOADER_CUTS, LOADER_MILLS };
	
	private CutAdapter mAdapter = null;
	private SimpleCursorAdapter mCurrentMillAdapter;
	private int mMillId;
	CursorLoader mCursorLoader = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cut);

		// TODO: This code is common to all BaseActivity derived classes... move it up
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_activity_cut);

		populate_header();
		setupMillSpinner();
		setViewState(ViewState.LOADING);

		ListView list = (ListView) findViewById(R.id.listViewCut);
		list.setAdapter(mAdapter = new CutAdapter(this, 0, new ArrayList<CutNode>()));
		list.setOnItemClickListener(this);
				
		mCursorLoader = new CursorLoader();
	}

	private void setViewState(ViewState loading) {
		Spinner currentMill = (Spinner) findViewById(R.id.spinnerCurrentMill);
		switch ( loading ) {
		case LOADING:
			findViewById(R.id.listViewCut).setVisibility(View.INVISIBLE);
			findViewById(R.id.progressBarCut).setVisibility(View.VISIBLE);	
			currentMill.setEnabled(false);
			return;
		case LOADED:
			findViewById(R.id.listViewCut).setVisibility(View.VISIBLE);
			findViewById(R.id.progressBarCut).setVisibility(View.GONE);
			currentMill.setEnabled(true);
			return;
		}
	}

	private void setupMillSpinner() {
		mCurrentMillAdapter = new SimpleCursorAdapter(
				this, android.R.layout.simple_spinner_item, 
				null, 
				new String[]{Mill.Fields.Name.name()}, 
				new int[]{android.R.id.text1}, 
				0);
		mCurrentMillAdapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		
		Spinner currentMill = (Spinner) findViewById(R.id.spinnerCurrentMill);
		currentMill.setAdapter(mCurrentMillAdapter);
		currentMill.setOnItemSelectedListener(this);
	}
	
	private void populate_header() {
		final Resources res = getResources();
		((TextView)findViewById(R.id.textViewCutCuts)).setText(res.getString(R.string.cutheader_cuts));
		((TextView)findViewById(R.id.textViewCutBf)).setText(res.getString(R.string.cutheader_bf));
		((TextView)findViewById(R.id.textViewCutValue)).setText(res.getString(R.string.cutheader_value));
	}

	@Override
	public void onDestroy() {
		getLoaderManager().destroyLoader(Loaders.LOADER_CUTS.ordinal());
		super.onDestroy();
	}
	
	@Override
	public Loader<List<CutNode>> onCreateLoader(int id, Bundle args) {
		Loader<List<CutNode>> loader = new AsyncTaskLoader<List<CutNode>>(this) {
			@Override
			public List<CutNode> loadInBackground() {
				ArrayList<Dimension> cuts = getIntent().getParcelableArrayListExtra(Util.CUTS);
				return getService().getCutPlans(cuts, mMillId);
			}
		};
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<CutNode>> loader, List<CutNode> cutPlans) {
		mAdapter.clear();
		mAdapter.addAll(cutPlans); //@@@ Makes copy of array
		mAdapter.notifyDataSetChanged();
		setViewState(ViewState.LOADED);
	}

	@Override
	public void onLoaderReset(Loader<List<CutNode>> loader) {
		mAdapter.clear();
		setViewState(ViewState.LOADING);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Intent intent = new Intent(this, MeasureActivity.class);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		int jobId = getIntent().getIntExtra(Util.JOB, -1);
		if ( jobId == -1 )
			Log.e(getClass().getSimpleName(), "JobId not in Intent");
		getService().addCutsToJob(mAdapter.getItem(position), jobId);
		finish();
	}
	
	@Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	  Spinner spinner = (Spinner) parent;
    switch ( spinner.getId() ) {
    case R.id.spinnerCurrentMill:
  		mAdapter.clear();
  		setViewState(ViewState.LOADING);
			mMillId = ((Cursor) parent.getItemAtPosition(pos)).getInt(0);
			getLoaderManager().restartLoader(Loaders.LOADER_CUTS.ordinal(), null, this);
			break;
    }
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
	
	private class CursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {

		public CursorLoader() {
			getLoaderManager().initLoader(Loaders.LOADER_MILLS.ordinal(), null, this);
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Loader<Cursor> loader; 
			final BuckDatabaseAdapter dB = BaseActivity.getService().getDbAdapter();
			loader = new SimpleCursorLoader(CutActivity.this, args) {
				@Override
				public Cursor loadInBackground() {
					return dB.fetchAll(Tables.Mills);
				};
			};
			loader.forceLoad();
			return loader;
		}
				
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				mCurrentMillAdapter.swapCursor(cursor);
		}
		
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
				mCurrentMillAdapter.swapCursor(null);
		}
	};

}
