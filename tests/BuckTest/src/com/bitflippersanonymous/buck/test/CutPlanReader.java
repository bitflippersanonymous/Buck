package com.bitflippersanonymous.buck.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.JsonReader;

public class CutPlanReader {

	public void readJsonStream(InputStream in) throws IOException {
	  JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	  try {
	    return readCutPlan(reader);
	  } finally {
	    reader.close();
	  }
	}
	
}
