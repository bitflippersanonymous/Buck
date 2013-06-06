package com.bitflippersanonymous.buck.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitflippersanonymous.buck.R;

import android.database.Cursor;
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
		mSummaries = new ArrayList<JobSummary>();
		Map<Integer, JobSummary> summaries = new HashMap<Integer, JobSummary>();
		
		if ( cursor == null || !cursor.moveToFirst() )
			return;
		do {
			JobSummary sum = new JobSummary(cursor);
			mSummaries.add(sum);
			
			Integer priceId = sum.getAsInteger(Cut.Fields.PriceId);
			if ( priceId == -1 ) continue;
			
			Integer millId = sum.getAsInteger(Cut.Fields.MillId);
			Integer value = sum.getAsInteger(Cut.Fields.Value);
			Integer fbm = sum.getAsInteger(Cut.Fields.FBM);

			JobSummary millTotal = summaries.get(millId);
			if ( millTotal == null ) {
				summaries.put(millId, millTotal = new JobSummary(millId, "Mill Total"));
			}

			millTotal.mCount++;
			millTotal.mValue += value;
			millTotal.mFBM += fbm;
			
		} while ( cursor.moveToNext() );
		
		mSummaries.addAll(summaries.values());
		Collections.sort(mSummaries, JobSummary.getByPriceId());
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
					R.layout.summary_entry, parent, false);

		StringBuilder rate = new StringBuilder(); 
		if ( summary.mRate > 0 )
			rate.append(summary.mRate);
		
		((TextView)convertView.findViewById(R.id.textViewSumName))
			.setText(summary.mName);
		((TextView)convertView.findViewById(R.id.textViewSumRate))
			.setText(rate);
		((TextView)convertView.findViewById(R.id.textViewSumCount))
			.setText(Integer.toString(summary.mCount));
		((TextView)convertView.findViewById(R.id.textViewSumBf))
			.setText(Integer.toString((summary.mFBM)));
		StringBuilder sb = new StringBuilder();
		sb.append("$").append(summary.mValue);
		((TextView)convertView.findViewById(R.id.textViewSumValue))
			.setText(sb);
		return convertView;	
	}

	@Override
	public long getItemId(int position) {
		return 0; // TODO: Return db id of Mill/Price?
	}

}