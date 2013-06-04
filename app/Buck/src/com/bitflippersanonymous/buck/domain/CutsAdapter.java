package com.bitflippersanonymous.buck.domain;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitflippersanonymous.buck.R;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CutsAdapter extends BaseAdapter implements ListAdapter {
	final static String TOTAL = "TOTAL";
	
	JobSummary mTotal;
	List<JobSummary> mSummaries;
	
	public CutsAdapter(Cursor cursor) {
		swapCursor(cursor);
	}

	// Note: This happens on the UI thread
	public void swapCursor(Cursor cursor) {
		mTotal = new JobSummary(TOTAL);
		mSummaries = new ArrayList<JobSummary>();

		Map<Integer, JobSummary> summaries = new HashMap<Integer, JobSummary>();
		summaries.put(-1, mTotal);
		
		if ( cursor == null || !cursor.moveToFirst() )
			return;
		do {
			Integer length = cursor.getInt(Cut.Fields.Length.ordinal());
			Integer value = cursor.getInt(Cut.Fields.Value.ordinal());
			Integer fbm = cursor.getInt(Cut.Fields.FBM.ordinal());

			JobSummary sum = summaries.get(length);
			if ( sum == null )
				summaries.put(length, sum = new JobSummary(Integer.toString(length)));

			sum.mValue += value;
			sum.mFBM += fbm;
			mTotal.mValue += value;
			mTotal.mFBM += fbm;
		} while ( cursor.moveToNext() );
		
		mSummaries.addAll(summaries.values());
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mSummaries.size();
	}

	@Override
	public JobSummary getItem(int position) {
		return mSummaries.get(position);
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		JobSummary summary = getItem(position);
		if ( convertView == null )
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.cut_entry, parent, false);
		
		((TextView)convertView.findViewById(R.id.textViewCutCuts))
			.setText(summary.getName());
		((TextView)convertView.findViewById(R.id.textViewCutBf))
			.setText(Integer.toString((summary.mFBM)));
		StringBuilder value = new StringBuilder();
		value.append("$").append(summary.mValue);
		((TextView)convertView.findViewById(R.id.textViewCutValue))
			.setText(value);
		return convertView;	
	}

	@Override
	public long getItemId(int position) {
		return 0; // TODO: Return db id of Mill/Price?
	}

}