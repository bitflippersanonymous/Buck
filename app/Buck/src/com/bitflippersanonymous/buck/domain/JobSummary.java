package com.bitflippersanonymous.buck.domain;

import java.util.Comparator;

public class JobSummary {
	
	final String mName;
	final int mPriceId;
	
	// Totals for this group
	public int mCount = 0;
	public int mFBM = 0;
	public int mValue = 0;
	
	public JobSummary(int priceId, String name) {
		mPriceId = priceId;
		mName = name;
	}

	public String getName() {
		return mName;
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
}
