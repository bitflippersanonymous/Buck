package com.bitflippersanonymous.buck.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.AsyncTask;
import android.util.Log;

import com.bitflippersanonymous.buck.domain.Util;

public class LoadTask extends AsyncTask<Util.FileReader, Void, Integer> {
	public void readFile(Util.FileReader reader) {
		try {
			InputStream instream = reader.getInputStream();
			if ( instream != null ) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				while (( line = buffreader.readLine()) != null) {
					reader.handleLine(line);
				}
			}
			instream.close();
		} catch (java.io.FileNotFoundException e) {
			Log.e(getClass().getSimpleName(), "File not found: " + reader.getFilename());
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Error reading: " + reader.getFilename());
		}
	}

	@Override
	protected Integer doInBackground(Util.FileReader... params) {
		readFile(params[0]);
		return 0;
	}
}
