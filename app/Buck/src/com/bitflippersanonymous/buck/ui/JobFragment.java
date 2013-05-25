package com.bitflippersanonymous.buck.ui;



import android.app.ActionBar;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.PriceAdapter;
import com.bitflippersanonymous.buck.domain.JobSummary;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;

public class JobFragment extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Job>, Util.Update {

	private Job mJob = null;
	
	public JobFragment() {}
	
    @Override 
    public  void  onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getLoaderManager().initLoader(0, null, this);

		View view = inflater.inflate(R.layout.fragment_job, container, false);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_menu, menu);
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


	@Override
	public Loader<Job> onCreateLoader(int id, Bundle args) {
		Loader<Job> loader = new AsyncTaskLoader<Job>(getActivity()) {
			@Override
			public Job loadInBackground() {
				return BaseActivity.getService().getJob(getArguments().getInt(Util._ID));
			}
		};
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Job> loader, Job job) {
		mJob = job;
		String jobName = mJob.getAsString(Job.Fields.Name);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(jobName);

		setListAdapter(new ArrayAdapter<JobSummary>(getActivity(), 0, job.getSummary()));
	}
	
	@Override
	public void onLoaderReset(Loader<Job> loader) {
		mJob = null;
	}
	
	@Override
	public void update() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
}