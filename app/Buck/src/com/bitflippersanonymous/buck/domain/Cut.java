package com.bitflippersanonymous.buck.domain;

import android.database.Cursor;

/*
 * A single piece added to database at end of CutActivity.  A log may result in
 * multiple pieces.  Value and Length are almost redundant to PriceId, but will
 * tell us the price and length when added to the database.  The user may change
 * the price after the log is cut. 
 * 
 */
public class Cut extends DbItem {
	public enum Fields { _id, JobId, MillId, PriceId,	Width, Length, FBM,	Value	};
	
	public Cut(Cursor cursor) {
		super(cursor);
	}

	// Into DB
	public Cut(int jobId, CutNode node) {
		put(Fields.JobId, jobId);
		put(Fields.MillId, node.getMillId());
		put(Fields.PriceId, node.getPriceId());
		put(Fields.Width, node.getDimension().getWidth());
		put(Fields.Length, node.getDimension().getLength());
		put(Fields.FBM, node.getBoardFeet());
		put(Fields.Value, node.getValue());
	}
	
	@Override
	public String getTableName() {
		return Util.DatabaseBase.Tables.Cuts.name();
	}
}
