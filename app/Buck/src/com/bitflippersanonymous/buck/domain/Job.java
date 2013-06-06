package com.bitflippersanonymous.buck.domain;
import android.database.Cursor;

public class Job extends DbItem  {
	public enum Fields {_id, Name};
	public static final String JOB = "Job";

	public Job(Cursor cursor) {
		super(cursor);
	}

	public Job() { }

	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Jobs.name();
	}
}
