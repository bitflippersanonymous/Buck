package com.bitflippersanonymous.buck.domain;

import java.util.HashMap;
import java.util.List;

import com.bitflippersanonymous.buck.domain.Mill.Fields;

import android.database.Cursor;

public class Mill extends DbItem {
	public enum Fields {Name};
	private static final Tag[] sTags = {
		new Tag(Fields.Name),
	};

	public static Tag[] getsTags() {
		return sTags;
	}

	private List<Price> mPrices = null;
	
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
	}

	public List<Price> getPrices() {
		return mPrices;
	}
}
