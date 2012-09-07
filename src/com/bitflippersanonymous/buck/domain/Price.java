package com.bitflippersanonymous.buck.domain;

import java.util.HashMap;

import android.database.Cursor;


public class Price extends DbItem {
	private static final Tags[] sTags = {
		new Tags("MillId", DataType.integer), 
		new Tags("Width", DataType.integer), 
		new Tags("Length", DataType.integer), 
		new Tags("Rate", DataType.integer), 
		new Tags("Price", DataType.integer)};
	
	public static Tags[] getsTags() {
		return sTags;
	}

	public Price(Cursor cursor) {
		super(sTags, cursor);
	}

	public Price(HashMap<String, String> data, int i) {
		super(sTags, data, i);
	}

	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Prices.name();
	}

}
