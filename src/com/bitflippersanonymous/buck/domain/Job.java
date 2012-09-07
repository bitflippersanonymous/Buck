package com.bitflippersanonymous.buck.domain;

import java.util.HashMap;

import android.database.Cursor;

public class Job extends DbItem {

	private static final Tags[] sTags = {
		new Tags("Name"),
	};

	public static Tags[] getsTags() {
		return sTags;
	}
	
	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Jobs.name();
	}
	
	public Job(Cursor cursor) {
		super(sTags, cursor);
	}

	public Job(HashMap<String, String> data, int i) {
		super(sTags, data, i);
	}

}
