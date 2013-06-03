package com.bitflippersanonymous.buck.domain;

import java.util.Comparator;

import android.database.Cursor;


public class Price extends DbItem<Price.Fields> {

	private int mLength;
	private int mTop;
	private int mPrice;
	private int mMillId;
	
	public enum Fields {MillId, Length, Rate, Top, Price};
	private static final Tag[] sTags = {
		new Tag(Fields.MillId, DataType.integer), 
		new Tag(Fields.Length, DataType.integer), 
		new Tag(Fields.Rate, DataType.integer),
		new Tag(Fields.Top, DataType.integer), 
		new Tag(Fields.Price, DataType.integer)};

	public static final String PRICE = "Price";
	
	public static Tag[] getsTags() {
		return sTags;
	}

	public Price(Cursor cursor) {
		super(sTags, cursor);
		mLength = getAsInteger(Price.Fields.Length);
		Integer top = getAsInteger(Price.Fields.Top);
		if ( top != null )
			mTop = top;
		else
			mTop = -1;
		mPrice = getAsInteger(Price.Fields.Price);
		mMillId = getAsInteger(Price.Fields.MillId);
	}

	public Price(int i) {
		super(sTags, i);
	}

	public int getMillId() {
		return mMillId;
	}
	
	public int getLength() {
		return mLength;
	}

	public int getTop() {
		return mTop;
	}

	public int getPrice() {
		return mPrice;
	}

	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Prices.name();
	}

	private static Comparator<? super Price> sByLength = null;
	public static Comparator<? super Price> getByLength() {
		if ( sByLength == null )
			sByLength = new Comparator<Price>(){
			@Override
			public int compare(Price lhs, Price rhs) {
				return lhs.getAsInteger(Price.Fields.Length) - rhs.getAsInteger(Price.Fields.Length);
			}};
		return sByLength;
	}


}
