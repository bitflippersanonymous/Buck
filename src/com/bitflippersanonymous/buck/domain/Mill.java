package com.bitflippersanonymous.buck.domain;

import java.util.HashMap;

import android.database.Cursor;

public class Mill extends DbItem {

	private static final Tags[] sTags = {
		new Tags("Name"),
	};

	public static Tags[] getsTags() {
		return sTags;
	}
	
	public Mill(Cursor cursor) {
		super(sTags, cursor);
	}

	public Mill(HashMap<String, String> data, int i) {
		super(sTags, data, i);
	}

	public String getTableName() {
		return Util.DatabaseBase.Tables.Mills.name();
	}
}
