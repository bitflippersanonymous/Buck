package com.bitflippersanonymous.buck.domain;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class Job extends DbItem<Job.Fields> {
	public enum Fields {Name};
	private static final Tag[] sTags = {
		new Tag(Fields.Name),
	};
	public static final String JOB = "Job";
	
	public static Tag[] getsTags() {
		return sTags;
	}

	private List<JobSummary> mSummary = new ArrayList<JobSummary>();

	
	public Job(Cursor cursor) {
		super(sTags, cursor);
	}

	public Job(int i) {
		super(sTags, i);
	}

	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Jobs.name();
	}

	// Returns a summary of all pieces cut
	public List<JobSummary> getSummary() {
		return mSummary;
	}

	public void setSummary(List<JobSummary> summary) {
		mSummary = summary;
	}

}
