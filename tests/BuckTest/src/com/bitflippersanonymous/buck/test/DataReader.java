package com.bitflippersanonymous.buck.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

class DataReader {
	private InputStream mInstream = null;
	private BufferedReader mBuffreader = null;
	private String mFilename;
	
	public DataReader(Context context, String filename) {
		mFilename = filename;
		try {
			AssetManager am = context.getAssets();
			mInstream = am.open(mFilename);
			mBuffreader = new BufferedReader(new InputStreamReader(mInstream));
		} catch (java.io.FileNotFoundException e) {
			Log.e(getClass().getSimpleName(), "File not found: " + mFilename);
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Error reading: " + mFilename);
		}
	}
	
	public List<Integer> handleLine(String line) {
		List<Integer> retVal = new ArrayList<Integer>();
		for ( String s : line.split("\\s*,\\s*")) {
			retVal.add(Integer.parseInt(s));
		}
		return retVal;
	}

	public List<Integer> readLine() {
		try {
			if ( mBuffreader != null ) {
				String line = mBuffreader.readLine();
				if ( line != null)
					return handleLine(line);
			}
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Error reading: " + mFilename);
		}
		return null;
	}

	public void close() {
		if ( mInstream == null ) return;

		try {
			mInstream.close();
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Error closing: " + mFilename);
		}
	}
}
