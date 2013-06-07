package com.bitflippersanonymous.buck.domain;

import java.util.Comparator;

import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Views;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class JobSummary extends DbItem {
	public enum Fields { PriceId, MillId, Name, Rate, Length, FBM, Value };

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
		mMillId = getAsInteger(Fields.PriceId);
		mName = getAsString(Fields.Name);
	}

	final static String mQuery = "SELECT millid, Name, " +
			"100 * count(priceid) / count AS rate, " +
			"Length, FBM, Value" +
			"FROM Cuts, Prices, job_totals " +
			"WHERE job_totals.jobid == cuts.jobid " +
			"AND cuts.priceid = price._id " +
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

