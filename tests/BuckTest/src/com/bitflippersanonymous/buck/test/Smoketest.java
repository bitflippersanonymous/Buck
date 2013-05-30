package com.bitflippersanonymous.buck.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.bitflippersanonymous.buck.domain.CutNode;
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
	
	/* Directly test widthAtPosition. Format of data file:
	 * measured width, length
	 * test length, expected width
	*/
	@MediumTest
	public void testWidthAtPosition() throws IOException {
		List<Dimension> dimensions = new ArrayList<Dimension>();
		DataReader dataReader = new DataReader(mTestContext, "test_width_interp.csv");
		
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

	@MediumTest
	public void testCutPlanner() {
		class Expected {
			public int mBoardFeet;
			public int mValue;
			public Expected(int bf, int value) {
				mBoardFeet = bf;
				mValue = value;
			}
		}
		
		List<Expected> expecteds = new ArrayList<Expected>();
		List<Dimension> dimensions = new ArrayList<Dimension>();
		DataReader dataReader = new DataReader(mTestContext, "test_cut_planner.csv");

		List<Integer> data = dataReader.readLine();
		assertNotNull(data);
		while ( data != null ) {
			dimensions.clear();
			for (int i = 0; i+1 < data.size(); i+=2 ) {
				dimensions.add(new Dimension(data.get(i), data.get(i+1)));
			}
			List<CutNode> cutNodes = mService.getCutPlans(dimensions, 1);

			// Build up line for data file so we can generate these
			StringBuilder str = new StringBuilder();
			for ( CutNode cutNode : cutNodes ) {
				int bf = cutNode.getTotalBoardFeet();
				int value = cutNode.getTotalValue();
				str.append(bf).append(", ").append(value).append(", ");
			}

			expecteds.clear();
			data = dataReader.readLine();
			for (int i = 0; i+1 < data.size(); i+=2 ) {
				expecteds.add(new Expected(data.get(i), data.get(i+1)));
			}
			
			assertTrue(cutNodes.size() == expecteds.size());
			for ( int i=0; i<cutNodes.size(); i++ ) {
				int bf = cutNodes.get(i).getTotalBoardFeet();
				int value = cutNodes.get(i).getTotalValue();
				Expected x = expecteds.get(i);
				if ( x.mBoardFeet != bf || x.mValue != value )
					fail();
			}
			data = dataReader.readLine();
		}
		dataReader.close();
	}

}
