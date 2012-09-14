package com.bitflippersanonymous.buck.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CutPlan {

	private List<Dimension> mWholeLogSize = null;  // Size of whole log
	private List<Dimension> mCuts = null;		// How to cut it
	private int mBoardFeet = 0;
	
	public CutPlan(CutPlan obj) {
		mWholeLogSize = obj.mWholeLogSize;
		mBoardFeet = obj.mBoardFeet;
		mCuts = new ArrayList<Dimension>(obj.mCuts);
	}
	
	public CutPlan(List<Dimension> wholeLogSize) {
		mWholeLogSize = wholeLogSize;
		mCuts = new ArrayList<Dimension>();
	}
	
	public List<Dimension> getStemDimensions() {
		return mWholeLogSize;
	}
	
	public CutPlan addCut(Dimension cut, int boardFeet) {
		mCuts.add(cut);
		mBoardFeet += boardFeet;
		return this;
	}
	
	public List<Dimension> getCuts() {
		return mCuts;
	}
	
	public int getBoardFeet() {
		return mBoardFeet;
	}
	
	private static Comparator<? super CutPlan> sByBoardFeet = null;
	public static Comparator<? super CutPlan> getByBoardFeet() {
		if ( sByBoardFeet == null )
			sByBoardFeet = new Comparator<CutPlan>(){
			@Override
			public int compare(CutPlan lhs, CutPlan rhs) {
				return rhs.getBoardFeet() - lhs.getBoardFeet();
			}};
		return sByBoardFeet;
	}
	
}
