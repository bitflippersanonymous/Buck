package com.bitflippersanonymous.buck.ui;



import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Fragment;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class JobFragment extends Fragment {

	public JobFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int jobId = getArguments().getInt(Util._ID);

		// This fetch could take a long time, so should be done as async task
		Cursor cursor = BaseActivity.getService().getDbAdapter().fetchEntry(Tables.Jobs, jobId);
		Job job = new Job(cursor);
		String jobName = job.getAsString(Job.Fields.Name);
		
		// FIXME: Job.getTags()[0] is the name, kind of hard to tell here.  Need better name
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(jobName);
		
		View view = inflater.inflate(R.layout.fragment_job, container, false);
		TextView t = (TextView)view.findViewById(R.id.textViewJob);
		t.setText(jobName);
		return view;
	}
}