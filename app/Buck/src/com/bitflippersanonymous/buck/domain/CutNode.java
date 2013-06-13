package com.bitflippersanonymous.buck.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import com.bitflippersanonymous.buck.domain.Util.DbContent;

public class CutNode {
	private CutNode mParent;
	private List<CutNode> mChildren = new ArrayList<CutNode>();
	private Dimension mDimension;
	private int mBoardFeet = 0;
	private Price mPrice;

	public CutNode() { } // Root node
	
	public CutNode(Dimension dimension, int boardFeet, Price price) {
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

	public Dimension getDimension() {
		return mDimension;
	}

	public int getMillId() {
		if ( mPrice == null )
			return -1;
		return mPrice.getMillId();
	}

	public int getPriceId() {
		if ( mPrice == null )
			return -1;
		return mPrice.getId();
	}
	
	public int getBoardFeet() {
		return mBoardFeet;
	}
	
	public int getTotalBoardFeet() {
		int total = 0;
		for ( CutNode node = this; node.mDimension != null; node = node.mParent ) {
			if ( node.mPrice != null )
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

	public int getValue() {
		if ( mPrice == null )
			return 0;
		return (int)(mBoardFeet * ((float)mPrice.getPrice() / 1000));
	}
	
	public int getTotalValue() {
		int total = 0;
		for ( CutNode node = this; node.mDimension != null; node = node.mParent ) {
			total += node.getValue();
		}
		return total;
	}

	public int getTotalLength(int kerfFeet) {
		int total = 0;
		for ( Dimension dim : getTotalCuts() )
			total += dim.getLength() + kerfFeet;
		return total;
	}

	public List<DbContent> getCutsList(int jobId) {
		List<DbContent> cuts = new ArrayList<DbContent>();
		for ( CutNode node = this; node.mDimension != null; node = node.mParent ) {
			cuts.add(new Cut(jobId, node));
		}
		return cuts;
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
