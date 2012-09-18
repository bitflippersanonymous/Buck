package com.bitflippersanonymous.buck.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;

import com.bitflippersanonymous.buck.domain.Util.DbTags;

public class Mill extends DbItem<Mill.Fields> {
	public enum Fields {Name, Enabled};
	private static final Tag[] sTags = {
		new Tag(Fields.Name),
		new Tag(Fields.Enabled, DbTags.DataType.integer),
	};
	public static final String MILL = "Mill";

	public static Tag[] getsTags() {
		return sTags;
	}

	private List<Price> mPrices = new ArrayList<Price>();
	
	public Mill(Cursor cursor) {
		super(sTags, cursor);
	}

	public Mill(int i) {
		super(sTags, i);
	}

	public String getTableName() {
		return Util.DatabaseBase.Tables.Mills.name();
	}
	
	public void setPrices(List<Price> prices) {
		mPrices  = prices;
		Collections.sort(mPrices, Price.getByLength());
	}

	public List<Price> getPrices() {
		return mPrices;
	}
	
}
