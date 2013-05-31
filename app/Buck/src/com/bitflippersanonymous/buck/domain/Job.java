package com.bitflippersanonymous.buck.domain;
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
}
