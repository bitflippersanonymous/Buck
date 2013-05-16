package com.bitflippersanonymous.buck.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.bitflippersanonymous.buck.domain.CutNode;
import com.bitflippersanonymous.buck.domain.CutPlan;
import com.bitflippersanonymous.buck.domain.CutPlanner;
import com.bitflippersanonymous.buck.domain.Dimension;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.service.BuckService;


public class Smoketest extends ServiceTestCase<BuckService> {
	static BuckService mService = null;
	static boolean mBound = false;
	static Boolean mReady = false;
	static Context mTestContext = null;

	public Smoketest() {
		super(BuckService.class);
	}

	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			synchronized(mReady) {
				mReady.notify();
			}
		}
	};

	static Messenger mMessenger = new Messenger(mHandler);
	
	ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			BuckService.LocalBinder binder = (BuckService.LocalBinder) service;
			mService = binder.getService();
			mService.addClient(mMessenger);
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			mBound = false;
		}		
	};
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mTestContext = getContext().createPackageContext("com.bitflippersanonymous.buck.test",
        Context.CONTEXT_IGNORE_SECURITY);
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), BuckService.class);
		boolean bound = getContext().bindService(startIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		assertTrue(bound);
		synchronized(mReady) {
			try {
				mReady.wait(5000);
			} catch (InterruptedException e) {
				fail();
			}
		}
		assertTrue(mBound);
		assertNotNull(mService);
	}

	@Override
	protected void tearDown() throws Exception {
	}
	
	/**
	 * Test Service init. Files were read and db populated
	 */
	@MediumTest
	public void testDBInit() {
		// Check database_init.xml loaded
		Mill mill = mService.getMill(1);
		String name = mill.getAsString(Mill.Fields.Name);
		assertNotNull(name);
		assertTrue(mill.getPrices().size() > 0);
		
		// Check scribner.csv loaded
		Dimension cut = new Dimension(0, 0);
		for ( int w = 4; w <= 30; w++ ) {
			for ( int l = 6; l <= 40; l++ ) {
				if ( w <= 4 && l <= 7 ) continue;
				cut.setLength(l);
				cut.setWidth(w);
				Integer bf = mService.getBoardFeet(cut);
				assertTrue(bf > 0);
			}
		}
	}
	
	@MediumTest
	public void testCutPlan() {
		List<Dimension> dimensions = new ArrayList<Dimension>();
		dimensions.add(new Dimension(30, 90));
		dimensions.add(new Dimension(20, 0));
		List<CutNode> cutNodes = mService.getCutPlans(dimensions);
		
		for ( CutNode node : cutNodes ) {
			int bf = node.getBoardFeet();
			assertTrue(bf > 500 && bf < 5000);
		}
	}

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
	

	/* Directly test widthAtPosition. Format of data file:
	 * measured width, length
	 * test length, expected width
	*/
	@MediumTest
	public void testCutPlanner() throws IOException {
		List<Dimension> dimensions = new ArrayList<Dimension>();
		DataReader dataReader = new DataReader(mTestContext, "test_cut_planner.csv");
		
		List<Integer> data = dataReader.readLine();
		assertNotNull(data);
		while ( data != null ) {
			dimensions.clear();
			for (int i = 0; i+1 < data.size(); i+=2 ) {
				dimensions.add(new Dimension(data.get(i), data.get(i+1)));
			}
			data = dataReader.readLine();
			for (int i = 0; i+1 < data.size(); i+=2 ) {
				assertEquals(CutPlanner.widthAtPosition(dimensions, data.get(i)), (int)data.get(i+1));
			}
			data = dataReader.readLine();
		}
		dataReader.close();
	}
}
