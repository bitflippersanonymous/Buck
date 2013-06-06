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
		mTotal = new JobSummary(-1, TOTAL);
		mSummaries = new ArrayList<JobSummary>();
		mSummaries.add(mTotal);
		
		Map<Integer, JobSummary> summaries = new HashMap<Integer, JobSummary>();
		
		if ( cursor == null || !cursor.moveToFirst() )
			return;
		do {
			Cut cut = new Cut(cursor);
			Integer priceId = cut.getAsInteger(Cut.Fields.PriceId);
			if ( priceId == -1 ) continue;
			
			Integer length = cut.getAsInteger(Cut.Fields.Length);
			Integer value = cut.getAsInteger(Cut.Fields.Value);
			Integer fbm = cut.getAsInteger(Cut.Fields.FBM);
			
			Integer rate = 0; // cut.getAsInteger(Cut.Fields.Rate);

			// Should sort and group by PriceId to separate by Mill.
			// FIXME: firewood FBM not set by CutPlanner, ignore for now
			JobSummary sum = summaries.get(priceId);
			if ( sum == null ) {
				StringBuilder name = new StringBuilder();
				name.append(length).append("'");
				summaries.put(priceId, sum = new JobSummary(priceId, name.toString()));
			}
			
			sum.mCount++;
			sum.mValue += value;
			sum.mFBM += fbm;
			mTotal.mCount++;
			mTotal.mValue += value;
			mTotal.mFBM += fbm;
		} while ( cursor.moveToNext() );
		
		mSummaries.addAll(summaries.values());
		for ( JobSummary summary : mSummaries ) {
			if ( summary.mRate != 0 ) {
				summary.mRate = summary.mCount / mTotal.mCount;
			}
		}
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
			.setText(summary.getName());
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