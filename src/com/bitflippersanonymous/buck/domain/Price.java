package com.bitflippersanonymous.buck.domain;

import java.util.HashMap;

import com.bitflippersanonymous.buck.domain.Mill.Fields;
import com.bitflippersanonymous.buck.domain.Util.DbTags.Tag;

import android.content.ContentValues;
import android.database.Cursor;


public class Price extends DbItem {
	
	public enum Fields {MillId, Width, Length, Rate, Price};
	private static final Tag[] sTags = {
		new Tag(Fields.MillId, DataType.integer), 
		new Tag(Fields.Width, DataType.integer), 
		new Tag(Fields.Length, DataType.integer), 
		new Tag(Fields.Rate, DataType.integer), 
		new Tag(Fields.Price, DataType.integer)};
	
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
