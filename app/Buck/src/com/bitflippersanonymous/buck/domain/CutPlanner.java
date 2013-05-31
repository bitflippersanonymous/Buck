package com.bitflippersanonymous.buck.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.SparseIntArray;

import com.bitflippersanonymous.buck.service.LoadTask;

public class CutPlanner {
  //TODO: Get from prefs
	
	private Map<Dimension, Integer> mScribnerTable = new HashMap<Dimension, Integer>();
	private List<CutNode> mCutNodes;
	private List<Dimension> mWholeLogSize = null;
	private Object mLock = new Object();
	private boolean mReady = false;
	private int mTotalLogLength;
	private int mKerfLength;
	private int mMinTopDiameter;
	private SparseIntArray mWidthCache;
	
	public CutPlanner(Context context, String filename) {
		loadScribner(context, filename);
	}
	
	
	public int getBoardFeet(Dimension dim) {
		waitTillReady();

		// Hashmap lookup here is slow. Would be better as 2D array.
		Integer bf = mScribnerTable.get(dim);
		if ( bf != null )
			return bf;

		// Untested
		double D = dim.getWidth();
		double L = dim.getLength();
		return (int) ((Math.pow(0.79 * D, 2) - 2D - 4) * (L / 16));
	}

	public void waitTillReady() {
		if ( mReady == true )
			return;

		synchronized(mLock) {
			try {
				while ( mReady == false )
					mLock.wait(5000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private void loadScribner(Context context, String filename) {
		Util.FileReader reader = new ScribnerReader(context, filename);
		LoadTask task = new LoadTask(){
			private void setReady() {
				synchronized(mLock) {
					mReady = true;
					mLock.notify();
				}
			}
			@Override
			protected void onCancelled() {
				setReady(); // Well, not really
			}
			@Override
			protected void onPostExecute(Integer result) {
				setReady();
			}
		};
		task.execute(reader);
	}

	class ScribnerReader implements Util.FileReader {
		List<Integer> mWidths;
		String mFilename;
		Context mContext;
		
		public ScribnerReader(Context context, String filename) {
			mFilename = filename;
			mContext = context;
		}
		
		@Override
		public void handleLine(String line) {
			List<Integer> ints = new ArrayList<Integer>();
			 for ( String s : line.split(","))
				 ints.add(Integer.parseInt(s));
			 if ( mWidths == null )
				 mWidths = ints;
			 else {
				 // FIXME: Check for nulls here, table may be incomplete
				 for ( int i = 1; i < ints.size(); i++ )
					 mScribnerTable.put(new Dimension(mWidths.get(i), ints.get(0)), ints.get(i));
			 }
		}

		@Override
		public InputStream getInputStream() throws IOException {
			AssetManager am = mContext.getAssets();
			return am.open(mFilename);
		}

		@Override
		public String getFilename() {
			return mFilename;
		}
	}

	private static double CosineInterpolate(double y1, double y2, double mu) {
		double mu2 = (1-Math.cos(mu*Math.PI))/2;
		return y1*(1-mu2)+y2*mu2;
	}

	public int widthAtPosition(int position) {
		int width = mWidthCache.get(position, -1);
		if ( width != -1 )
			return width;
		width = widthAtPosition(mWholeLogSize, position);
		mWidthCache.put(position, width);
		return width;
	}
	
	// Need to find points before and after position to interpolate width
	public static int widthAtPosition(List<Dimension> wholeLogSize, int position) {
		if ( position == 0 )
			return wholeLogSize.get(0).getWidth();
		
		int i = 0;
		int totalLength = 0;
		int size = wholeLogSize.size();
		while ( i < size && position > totalLength) {
			totalLength += wholeLogSize.get(i++).getLength();
		}
		
		if ( position > totalLength )
			return 0;
		
		final Dimension d1 = wholeLogSize.get(i-1);
		final	Dimension d2 = wholeLogSize.get(Math.min(i, size-1));
		final int d1Length = d1.getLength();
		
		double mu = (double)(totalLength - d1Length + position) / d1Length;
		int width = (int)CosineInterpolate(d1.getWidth(), d2.getWidth(), mu);
		return width;
	}

	private void recCutPlan(CutNode parent, int position, Mill mill) {
		
		
		for ( Price price : mill.getPrices() ) {
			int length = price.getLength();
			int minWidth = price.getTop();
			if ( minWidth == -1 )
				minWidth = mMinTopDiameter;
			
			int newPosition = position + length + mKerfLength;
			int width = widthAtPosition(newPosition);

			if ( width >= minWidth ) {
				Dimension dim = new Dimension(width, length);
				CutNode newNode = new CutNode(dim, getBoardFeet(dim), 
						price.getPrice(), mill.getId());
				parent.addChild(newNode);
				
				recCutPlan(newNode, newPosition, mill);
			}
		}

		// Couldn't add any more, so we're at a leaf. Rest is firewood.
		if ( parent.getChildren().size() == 0 ) {
			CutNode scrapNode = calcScrapNode(position, mill);
			parent.addChild(scrapNode);
			mCutNodes.add(scrapNode);
		}
	}

	private CutNode calcScrapNode(int position, Mill mill) {
		int scrapLength = mTotalLogLength - position;
		int scrapWidth = widthAtPosition(mTotalLogLength);
		Dimension scrapDim = new Dimension(scrapWidth, scrapLength);
		CutNode scrapNode = new CutNode(scrapDim, 0, 0, mill.getId());
		return scrapNode;
	}

	private static int sumLogLength(List<Dimension> dims) {
		int total = 0;
		for ( Dimension dim : dims ) {
			total += dim.getLength();
		}
		return total;
	}
	
	// Doesn't cleanup unneeded nodes in the tree
	// needs to better handle same value nodes wrt price run rate. May be
	// hiding options
	public List<CutNode> getCutPlans(Mill mill, List<Dimension> wholeLogSize, 
			int kerfLength, int minTopDiameter) {
		waitTillReady();
		mCutNodes = new ArrayList<CutNode>();
		
		mWholeLogSize = wholeLogSize;
		mTotalLogLength = sumLogLength(mWholeLogSize);
		mKerfLength = kerfLength;
		mMinTopDiameter = minTopDiameter;
		mWidthCache = new SparseIntArray(mTotalLogLength);
		
		switch ( mWholeLogSize.size() ) {
		case 0:
			return mCutNodes;
		case 1:
			mWholeLogSize.add(new Dimension(mMinTopDiameter, 0));
		}
			
		recCutPlan(new CutNode(), 0, mill);
		
		Collections.sort(mCutNodes, CutNode.getByTotalValue());
		return mCutNodes;
	}
}