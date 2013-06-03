package com.bitflippersanonymous.buck.domain;

public class JobSummary {
	
	final String mName;
	
	// Totals for this group
	public int mCount = 0;
	public int mFBM = 0;
	public int mValue = 0;
	
	public JobSummary(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}
}
