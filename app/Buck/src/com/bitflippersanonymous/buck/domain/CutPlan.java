package com.bitflippersanonymous.buck.domain;

import java.util.List;

public class CutPlan {

	private List<Cut> mStemDimensions = null;  // Size of whole log
	private List<Integer> mCuts = null;		// How to cut it
	private int mBoardFeet = 0;
	
	public CutPlan(List<Cut> stem) {
		mStemDimensions = stem;
	}
	
	public List<Cut> getStemDimensions() {
		return mStemDimensions;
	}
	
	public CutPlan setCuts(List<Integer> cuts) {
		mCuts = cuts;
		return this;
	}
	
	public List<Integer> getCuts() {
		return mCuts;
	}
	
	public int getBoardFeet() {
		return mBoardFeet;
	}
	
	public CutPlan setBoardFeet(int boardFeet) {
		mBoardFeet = boardFeet;
		return this;
	}

}
