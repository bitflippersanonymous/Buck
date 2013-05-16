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
	Integer mPrice;
	
	// Root node has null mDimension
	public CutNode() {}
	
	public CutNode(Dimension dimension, int boardFeet, Integer price) {
		mDimension = dimension;
		mBoardFeet = boardFeet;
		mPrice = price;
	}


	public CutNode getParent() {
		return mParent;
	}
	
	public void addChild(CutNode child) {
		mChildren.add(child);
		child.mParent = this;
	}
	
	public void rmChild(CutNode child) {
		mChildren.remove(child);
	}

	public List<CutNode> getChildren() {
		return mChildren;
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

	public int getTotalValue() {
		return (int)(getTotalBoardFeet() * ((float)mPrice / 1000));
	}

	public int getTotalLength() {
		int total = 0;
		for ( Dimension dim : getTotalCuts() )
			total += dim.getLength();
		return total;
	}
	
	// FIXME: re-lookup of total BF for every comparison
	private static Comparator<? super CutNode> sByTotalBoardFeet = null;
	public static Comparator<? super CutNode> getByTotalBoardFeet() {
		if ( sByTotalBoardFeet == null )
			sByTotalBoardFeet = new Comparator<CutNode>(){
			@Override
			public int compare(CutNode lhs, CutNode rhs) {
				return rhs.getTotalBoardFeet() - lhs.getTotalBoardFeet();
			}};
		return sByTotalBoardFeet;
	}

	// FIXME: re-lookup of total BF for every comparison
	private static Comparator<? super CutNode> sByTotalValue = null;
	public static Comparator<? super CutNode> getByTotalValue() {
		if ( sByTotalValue == null )
			sByTotalValue = new Comparator<CutNode>(){
			@Override
			public int compare(CutNode lhs, CutNode rhs) {
				return rhs.getTotalValue() - lhs.getTotalValue();
			}};
		return sByTotalValue;
	}



	
}
