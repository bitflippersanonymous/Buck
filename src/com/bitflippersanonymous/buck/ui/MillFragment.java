package com.bitflippersanonymous.buck.ui;


// FIXME: Lots of duplicate code here between mill and job fragments.
import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Util;

import android.database.Cursor;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MillFragment extends Fragment {
	public MillFragment() {	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int millId = getArguments().getInt(Util._ID);

		// This fetch could take a long time, so should be done as async task
		Mill mill = BaseActivity.getService().getMill(millId);
		String millName = mill.get(mill.getTags()[0].getKey());
		
		// FIXME: Job.getTags()[0] is the name, kind of hard to tell here.  Need better name
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(millName);

		View view = inflater.inflate(R.layout.fragment_mill, container, false);
		TextView t = (TextView)view.findViewById(R.id.textViewMill);
		t.setText(millName);
	
		//TODO: set adapter on R.id.listViewMill
		return view;
	}
}