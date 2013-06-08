package com.bitflippersanonymous.buck.domain;

import java.util.Comparator;

import android.database.Cursor;

public class JobSummary extends DbItem {
	public enum Fields { PriceId, MillId, Length, Count, Rate, FBM, Value };

	public final String mName;
	public final int mPriceId;
	public final int mMillId;
	
	// Totals for this group
	public int mCount = 1;
	public int mFBM = 0;
	public int mValue = 0;
	public int mRate = 0;
	
	public JobSummary(int millId, String name) {
		mPriceId = -1;
		mMillId = millId;
		mName = name;
	}

	public JobSummary(Cursor cursor) {
		super(cursor);
		mPriceId = getAsInteger(Fields.PriceId);
		mMillId = getAsInteger(Fields.MillId);
		mName = getAsString(Fields.Length);
		mCount = getAsInteger(Fields.Count);
		mRate = getAsInteger(Fields.Rate);
		mFBM = getAsInteger(Fields.FBM);
		mValue = getAsInteger(Fields.Value);
	}
	
	final static String mQuery = 
			"SELECT cuts.PriceId, cuts.MillId, prices.Length, count(cuts._id) AS Count, " +
			"100 * count(priceid) / count AS Rate, " +
			"FBM, Value " +
			"FROM Cuts, Prices, job_totals " +
			"WHERE job_totals.jobid == cuts.jobid " +
			"AND cuts.priceid = prices._id " +
			"AND cuts.jobid == ? " +
			"GROUP BY cuts.priceid";
	
	public static String getSql() {
		return mQuery;
	}
	
	private static Comparator<? super JobSummary> sByPriceId = null;
	public static Comparator<? super JobSummary> getByPriceId() {
		if ( sByPriceId == null )
			sByPriceId = new Comparator<JobSummary>(){
			@Override
			public int compare(JobSummary lhs, JobSummary rhs) {
				return rhs.mPriceId - lhs.mPriceId;
			}};
		return sByPriceId;
	}

	@Override
	public String getTableName() {
		return null;
	}

	
}

/*SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
builder.setTables("Cuts JOIN Job_Totals ON Cuts.jobid == Job_Totals.jobid");

//query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, 
//String groupBy, String having, String sortOrder)

final String[] projectionIn = new String[]{"Length", "100 * count(priceid) / count as rate"};

Cursor cursor = builder.query(
		db, 
		projectionIn,
		null, null, 
		"Cuts.PriceId",
		null, null, null);
 */

