package com.bitflippersanonymous.buck.domain;

import java.util.Comparator;

import android.database.Cursor;

public class JobSummary extends DbItem {
	public enum Fields { _id, PriceId, MillId, Name, Rate, Length, FBM,	Value	};

	public final String mName;
	public final int mPriceId;
	
	// Totals for this group
	public int mCount = 1;
	public int mFBM = 0;
	public int mValue = 0;
	public int mRate = 0;
	
	public JobSummary(int millId, String name) {
		mPriceId = priceId;
		mName = name;
	}

	public JobSummary(Cursor cursor) {
		super(cursor);
		mPriceId = getAsInteger(Fields.PriceId);
		mName = getAsString(Fields.Name);
	}
	
	private static Comparator<? super JobSummary> sByPriceId = null;
	public static Comparator<? super JobSummary> getByPriceId() {
		if ( sByPriceId == null )
			sByPriceId = new Comparator<JobSummary>(){
			@Override
			public int compare(JobSummary lhs, JobSummary rhs) {
				return rhs.mPriceId - lhs.mPriceId;
			}};
		return sByPriceId;
	}

	@Override
	public String getTableName() {
		return null;
	}
}
