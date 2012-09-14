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
import android.util.Log;

import com.bitflippersanonymous.buck.service.LoadTask;


public class CutPlanner {
	private static final Integer mMinWidth = 4; // Get from prefs?
	private Map<Dimension, Integer> mScribnerTable = new HashMap<Dimension, Integer>();
	private Mill mMill = null;
	private List<CutPlan> mPlans = new ArrayList<CutPlan>();
	private List<Dimension> mWholeLogSize = null;
	private Object mLock = new Object();
	private boolean mReady = false;
	
	public CutPlanner(Context context, String filename) {
		loadScribner(context, filename);
	}
	
	public int getBoardFeet(Dimension dim) {
		waitTillReady();

		Integer bf = mScribnerTable.get(dim);
		if ( bf != null )
			return bf;
		
		Log.e(getClass().getSimpleName(), "Scribner Table Incomplete: " + dim);
		return 0;
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

	private int widthAtPosition(int position) {
		Collections.sort(mWholeLogSize, Dimension.getByLength());
		Dimension start = mWholeLogSize.get(0);
		Dimension end = mWholeLogSize.get(mWholeLogSize.size()-1);
		int width = start.getWidth() - (int)(((float)start.getWidth() / (float)(end.getLength() * 12)) * (position * 12));
		return width;
	}
	
	private void recCutPlan(CutPlan plan, int position) {
		
		for ( Price price : mMill.getPrices() ) {
			int length = price.getAsInteger(Price.Fields.Length);
			int newPosition = position+length;
			int width = widthAtPosition(newPosition);
			Integer minWidth = price.getAsInteger(Price.Fields.Top);
			if ( minWidth == null )
				minWidth = mMinWidth;
			
			if ( width > minWidth ) {
				Dimension dimension = new Dimension(width, length);
				plan.addCut(dimension, getBoardFeet(dimension));
				mPlans.add(plan);
				recCutPlan(new CutPlan(plan), newPosition);
			}				
		}
		
	}
		
	public List<CutPlan> getCutPlans(Mill mill, List<Dimension> wholeLogSize) {
		waitTillReady();

		mPlans.clear();
		mMill = mill;
		mWholeLogSize = wholeLogSize;

		recCutPlan(new CutPlan(mWholeLogSize), 0);
		return mPlans;
	}
}