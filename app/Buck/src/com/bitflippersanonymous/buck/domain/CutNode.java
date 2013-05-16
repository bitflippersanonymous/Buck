package com.bitflippersanonymous.buck.domain;

import java.util.ArrayList;
import java.util.List;

public class CutNode {
	private CutNode mParent;
	private List<CutNode> mChildren = new ArrayList<CutNode>();
	Dimension mDimension;
	
	// Root node
	public CutNode() {}
	
	public CutNode(Dimension dimension) {
		mDimension = dimension;
	}

	public void addChild(CutNode child) {
		mChildren.add(child);
		child.mParent = this;
	}

	public List<Dimension> getCuts() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBoardFeet() {
		// TODO Auto-generated method stub
		return 0;
	}

}
