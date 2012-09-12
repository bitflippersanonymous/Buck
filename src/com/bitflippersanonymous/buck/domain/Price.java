package com.bitflippersanonymous.buck.domain;

import android.database.Cursor;


public class Price extends DbItem<Price.Fields> {
	
	public enum Fields {MillId, Length, Rate, Price};
	private static final Tag[] sTags = {
		new Tag(Fields.MillId, DataType.integer), 
		new Tag(Fields.Length, DataType.integer), 
		new Tag(Fields.Rate, DataType.integer), 
		new Tag(Fields.Price, DataType.integer)};

	public static final String PRICE = "Price";
	
	public static Tag[] getsTags() {
		return sTags;
	}

	public Price(Cursor cursor) {
		super(sTags, cursor);
	}

	public Price(int i) {
		super(sTags, i);
	}

	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Prices.name();
	}

}
