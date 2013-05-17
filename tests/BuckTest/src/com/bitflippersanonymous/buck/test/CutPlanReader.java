package com.bitflippersanonymous.buck.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.bitflippersanonymous.buck.domain.CutNode;
import com.bitflippersanonymous.buck.domain.Dimension;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;
import android.util.Log;

public class CutPlanReader {
	
	public class Values {
		public int mBoardFeet;
		public int mValue;
	}
	
	public class CutPlan {
		public List<Dimension> mDims = new ArrayList<Dimension>();
		public List<Values> mVals = new ArrayList<Values>();
	}

	List<CutPlan> mCutPlans = null;
	
	public List<CutPlan> getCutPlans() {
		return mCutPlans; 
	}
	
	public CutPlanReader(Context context, String filename) {
		InputStream instream = null;
		try {
			AssetManager am = context.getAssets();
			instream = am.open(filename);
			mCutPlans = readJsonStream(instream);
		} catch (java.io.FileNotFoundException e) {
			Log.e(getClass().getSimpleName(), "File not found: " + filename);
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Error reading: " + filename);
		} finally {
			try {
				if ( instream != null )
					instream.close();
			} catch (IOException e) {
				Log.e(getClass().getSimpleName(), "Error closing: " + filename);
			}
		}
	}

	private CutPlan readCutPlan(JsonReader reader) throws IOException {
		CutPlan cutPlan = new CutPlan();
		reader.beginObject();
	     while ( reader.hasNext() ) {
	       String name = reader.nextName();
	       if (name.equals("dims")) {
	    	   reader.beginArray();
	    	   while ( reader.hasNext() ) {
	    		   cutPlan.mDims.add(Dimension.readJson(reader));
	    	   }
	    	   reader.endArray();
	       } else if (name.equals("vals")) {
	    	   reader.beginArray();
	    	   while ( reader.hasNext() ) {
	    		   reader.beginArray();
	    		   Values vals = new Values();
	    		   vals.mBoardFeet = reader.nextInt();
	    		   vals.mValue = reader.nextInt();
	    		   cutPlan.mVals.add(vals);
	    		   reader.endArray();
	    	   }
	    	   reader.endArray();
	       } else {
	    	   reader.skipValue();
	       }
	     }
	    reader.endObject();
	    return cutPlan;
	}
	
	private List<CutPlan> readCutPlansArray(JsonReader reader) throws IOException {
		List<CutPlan> cutPlans = new ArrayList<CutPlan>();
		reader.beginArray();
		while (reader.hasNext()) {
			cutPlans.add(readCutPlan(reader));
		}
		reader.endArray();
		return cutPlans;
	}

	public List<CutPlan> readJsonStream(InputStream in) throws IOException {
	  JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	  try {
		  return readCutPlansArray(reader);
	  } finally {
	    reader.close();
	  }
	}
	
}
