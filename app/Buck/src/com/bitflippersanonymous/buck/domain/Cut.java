package com.bitflippersanonymous.buck.domain;
import android.database.Cursor;

/*
 * A single piece added to database at end of CutActivity.  A log may result in
 * multiple pieces.  Value and Length are almost redundant to PriceId, but will
 * tell us the price and length when added to the database.  The user may change
 * the price after the log is cut. 
 * 
 */
public class Cut extends DbItem<Cut.Fields> {
	public enum Fields {JobId, MillId, PriceId, Width, Length, FBM, Value };
	private static final Tag[] sTags = {
			new Tag(Fields.JobId, DataType.integer),
			new Tag(Fields.MillId, DataType.integer),
			new Tag(Fields.PriceId, DataType.integer),
			new Tag(Fields.Width, DataType.integer),
			new Tag(Fields.Length, DataType.integer),
			new Tag(Fields.FBM, DataType.integer),
			new Tag(Fields.Value, DataType.integer),
	};
	
	public static Tag[] getsTags() {
		return sTags;
	}
	
	public Cut(Cursor cursor) {
		super(sTags, cursor);
	}

	public Cut(int i) {
		super(sTags, i);
	}

	public Cut(int jobId, CutNode node) {
		super(sTags, -1);
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
