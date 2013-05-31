package com.bitflippersanonymous.buck.domain;
import android.database.Cursor;

public class Cut extends DbItem<Cut.Fields> {
	public enum Fields {JobId, MillId, Width, Length, FBM, Value };
	private static final Tag[] sTags = {
			new Tag(Fields.JobId, DataType.integer),
			new Tag(Fields.MillId, DataType.integer),
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

	public Cut(CutNode node) {
		super(sTags, -1);
		put(Fields.MillId, node.getMillId());
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
