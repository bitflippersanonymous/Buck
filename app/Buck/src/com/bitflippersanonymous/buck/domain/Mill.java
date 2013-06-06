package com.bitflippersanonymous.buck.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;

public class Mill extends DbItem  {
	public enum Fields {_id, Name, Enabled};
	public static final String MILL = "Mill";
	
	private List<Price> mPrices = new ArrayList<Price>();
	
	public Mill(Cursor cursor) {
		super(cursor);
	}

	public Mill() {	}

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
