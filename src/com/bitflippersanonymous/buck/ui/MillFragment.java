package com.bitflippersanonymous.buck.ui;


import java.util.ArrayList;
import java.util.List;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.Cut;
import com.bitflippersanonymous.buck.domain.CutPlan;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Price;
import com.bitflippersanonymous.buck.domain.PriceAdapter;
import com.bitflippersanonymous.buck.domain.SimpleCursorLoader;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.service.BuckService;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MillFragment extends ListFragment
	implements LoaderManager.LoaderCallbacks<Mill> {
	public MillFragment() {	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);

		getLoaderManager().initLoader(0, null, this);
		return inflater.inflate(R.layout.fragment_mill, container, false);
	}
	
	@Override
	public Loader<Mill> onCreateLoader(int id, Bundle args) {
		Loader<Mill> loader = new AsyncTaskLoader<Mill>(getActivity()) {
			@Override
			public Mill loadInBackground() {
				return BaseActivity.getService().getMill(getArguments().getInt(Util._ID));
			}
		};
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Mill> loader, Mill mill) {
		String millName = mill.getAsString(Mill.Fields.Name);
		TextView t = (TextView)getView().findViewById(R.id.textViewMill);
		t.setText(millName);
		
		// Create new adapter. Alternatively, clear and addAll
		setListAdapter(new PriceAdapter(getActivity(), 0, mill.getPrices()));
	}

	@Override
	public void onLoaderReset(Loader<Mill> loader) {
		//??? Clear PriceAdapter?
	}

}