package com.bitflippersanonymous.buck.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.JobSummary.ViewType;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CutsAdapter extends BaseAdapter implements ListAdapter {
	private static final int MILLNAMELENGTH = 16;

	final static String MILL_TOTAL = "Mill Total";
	
	JobSummary mTotal;
	List<JobSummary> mSummaries;
	
	public CutsAdapter(Cursor cursor) {
		swapCursor(cursor);
	}

	public void swapCursor(Cursor cursor) {
		mSummaries = new ArrayList<JobSummary>();
		Map<Integer, JobSummary> millTotals = new HashMap<Integer, JobSummary>();
		
		if ( cursor == null || !cursor.moveToFirst() )
			return;
		do {
			JobSummary sum = new JobSummary(cursor);
			mSummaries.add(sum);

			JobSummary millTotal = getMillTotal(millTotals, sum);
			millTotal.mCount += sum.mCount;
			millTotal.mValue += sum.mValue;
			millTotal.mFBM += sum.mFBM;
			
		} while ( cursor.moveToNext() );
		
		mSummaries.addAll(millTotals.values());
		Collections.sort(mSummaries, JobSummary.getByMillId());
		notifyDataSetChanged();
	}

	private JobSummary getMillTotal(Map<Integer, JobSummary> millTotals,
			JobSummary sum) {
		JobSummary millTotal = millTotals.get(sum.mMillId);
		if ( millTotal == null ) {
			millTotals.put(sum.mMillId, millTotal = new JobSummary(sum.mMillId, sum.mMillName));
		}
		return millTotal;
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
	public int getItemViewType(int position) {
		return getItem(position).mViewType.ordinal();
	}
	
	@Override
	public int getViewTypeCount() {
		return JobSummary.ViewType.values().length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		JobSummary summary = getItem(position);
		if ( convertView == null )
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.summary_entry, parent, false);

		StringBuilder rate = new StringBuilder(); 
		StringBuilder total = new StringBuilder();
		switch ( ViewType.values()[getItemViewType(position)]) {
		case PieceCount:
			total.append(summary.mLength + "'");

			rate.append(summary.mCutRate);
			if ( summary.mPriceRate != null ) {
				// TODO: Set icon indicating above or below desired rate
			}
			break;
		case JobCount:
			total.append(summary.mMillName);
			if ( total.length() > MILLNAMELENGTH ) {
				total.setLength(MILLNAMELENGTH); 
				total.append("...");
			}
		}
		
		((TextView)convertView.findViewById(R.id.textViewSumName))
			.setText(total);
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
		
		if ( getItemViewType(position) == JobSummary.ViewType.JobCount.ordinal() ) {
			// TODO: Add drawable under row to make bold line for Total
		}
		
		return convertView;	
	}

	@Override
	public long getItemId(int position) {
		return mSummaries.get(position).mPriceId;
	}

}