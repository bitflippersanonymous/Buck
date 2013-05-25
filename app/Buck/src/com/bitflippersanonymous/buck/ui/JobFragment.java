package com.bitflippersanonymous.buck.ui;



import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;

public class JobFragment extends Fragment {

	public JobFragment() {}
	
    @Override 
    public  void  onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
    }
	
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
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Intent intent = new Intent(getActivity(), MeasureActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}