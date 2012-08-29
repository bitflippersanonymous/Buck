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
		int rowId = getArguments().getInt(Util._ID);
		String table = Util.DatabaseBase.Tables.Mills.name();

		// This fetch could take a long time, so should be done as async task
		Cursor cursor = BaseActivity.getService().getDbAdapter().fetchEntry(table, rowId);
		Mill mill = Mill.cursorToItem(cursor);
		
		// FIXME: Job.getTags()[0] is the name, kind of hard to tell here.  Need better name
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mill.get(Mill.getTags()[0]));

		View view = inflater.inflate(R.layout.fragment_mill, container, false);
		TextView t = (TextView)view.findViewById(R.id.textViewMill);
		t.setText(mill.get(Mill.getTags()[0]));
		return view;
	}
}