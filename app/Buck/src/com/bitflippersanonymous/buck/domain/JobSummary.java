package com.bitflippersanonymous.buck.domain;

import java.util.Comparator;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class JobSummary extends DbItem {
	enum ViewType { PieceCount, JobCount };
	
	// These go directly to the sql SELECT
	public enum Fields { 
		PriceId("Cuts.PriceId"), 
		MillId("Cuts.MillId"),
		MillName("Mills.Name"),
		Length("Prices.Length"),
		Count("count(cuts._id)"), 
		PriceRate("Prices.Rate"),
		CutRate("100 * count(priceid) / job_totals.count"),
		FBM, Value;
		
		final String mSelect;
		Fields(String select) {
			mSelect = select;
		}
		Fields() {
			mSelect = name();
		}
		String select() {
			return mSelect;
		}
	};

	public final ViewType mViewType;
	public final String mLength;
	public final String mMillName;
	public final int mPriceId;
	public final int mMillId;
	public int mCount = 0;
	public int mFBM = 0;
	public int mValue = 0;
	public Integer mPriceRate = null;
	public int mCutRate = 0;
	
	
	// Used for the Mill Total rows
	public JobSummary(int millId, String millName) {
		mViewType = ViewType.JobCount;
		mPriceId = -1;
		mMillId = millId;
		mMillName = millName;
		mLength = null;
	}

	public JobSummary(Cursor cursor) {
		super(cursor);
		mViewType = ViewType.PieceCount;
		mPriceId = getAsInteger(Fields.PriceId);
		mMillId = getAsInteger(Fields.MillId);
		mMillName = getAsString(Fields.MillName);
		mLength = getAsString(Fields.Length);
		mCount = getAsInteger(Fields.Count);
		mPriceRate = getAsInteger(Fields.PriceRate);
		mCutRate = getAsInteger(Fields.CutRate);
		mFBM = getAsInteger(Fields.FBM);
		mValue = getAsInteger(Fields.Value);
	}
	
	final static String mQuery;
	static {
		String tables = "Cuts, Prices, Job_totals, Mills";
		Fields[] values = Fields.values();
		String[] columns = new String[values.length];
		for ( int i=0; i<values.length; i++ ) {
			columns[i] =  new String(values[i].select() + " AS " + values[i].name());
		}
		String where = "job_totals.jobid == cuts.jobid " +
				"AND cuts.priceid = prices._id " +
				"AND prices.millid = mills._id " +
				"AND cuts.jobid == ? ";
		String groupBy = "cuts.priceid";
		String orderBy = "Prices.MillId";
		mQuery = SQLiteQueryBuilder.buildQueryString(false, tables, columns, where, groupBy, null, orderBy, null);
	};
	
	public static String getSql() {
		return mQuery;
	}
	
	private static Comparator<? super JobSummary> sByMillId = null;
	public static Comparator<? super JobSummary> getByMillId() {
		if ( sByMillId == null )
			sByMillId = new Comparator<JobSummary>(){
			@Override
			public int compare(JobSummary lhs, JobSummary rhs) {
				if ( rhs.mMillId == lhs.mMillId ) {
					return rhs.mPriceId - lhs.mPriceId;
				}
				return rhs.mMillId - lhs.mMillId;
			}};
		return sByMillId;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
