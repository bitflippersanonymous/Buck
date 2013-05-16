package com.bitflippersanonymous.buck.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class CutNode {
	private CutNode mParent;
	private List<CutNode> mChildren = new ArrayList<CutNode>();
	Dimension mDimension;
	int mBoardFeet;
	
	// Root node has null mDimension
	public CutNode() {}
	
	public CutNode(Dimension dimension, int boardFeet) {
		mDimension = dimension;
		mBoardFeet = boardFeet;
	}

	public void addChild(CutNode child) {
		mChildren.add(child);
		child.mParent = this;
	}

	public int getTotalBoardFeet() {
		int total = 0;
		for ( CutNode node = this; node.mDimension != null; node = node.mParent ) {
			total += node.mBoardFeet;
		}
		return total;
	}

	public List<Dimension> getTotalCuts() {
		ArrayList<Dimension> ret = new ArrayList<Dimension>();
		for ( CutNode node = this; node.mDimension != null; node = node.mParent ) {
			ret.add(node.mDimension);
		}
		Collections.reverse(ret);
		return ret;
	}
	
	// FIXME: re-lookup of total BF for every comparison
	private static Comparator<? super CutNode> sByBoardFeet = null;
	public static Comparator<? super CutNode> getByTotalBoardFeet() {
		if ( sByBoardFeet == null )
			sByBoardFeet = new Comparator<CutNode>(){
			@Override
			public int compare(CutNode lhs, CutNode rhs) {
				return rhs.getTotalBoardFeet() - lhs.getTotalBoardFeet();
			}};
		return sByBoardFeet;
	}
	
}
