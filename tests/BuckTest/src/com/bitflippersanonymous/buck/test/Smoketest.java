package com.bitflippersanonymous.buck.test;

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

import com.bitflippersanonymous.buck.domain.CutPlan;
import com.bitflippersanonymous.buck.domain.CutPlanner;
import com.bitflippersanonymous.buck.domain.Dimension;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.service.BuckService;


public class Smoketest extends ServiceTestCase<BuckService> {
	static BuckService mService = null;
	static boolean mBound = false;
	static Boolean mReady = false;


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
		List<CutPlan> cutPlans = mService.getCutPlans(dimensions);
		
		for ( CutPlan plan : cutPlans ) {
			int bf = plan.getBoardFeet();
			assertTrue(bf > 500 && bf < 5000);
		}
	}
	
	@MediumTest
	public void testCutPlanner() {
		List<Dimension> dimensions = new ArrayList<Dimension>();
		dimensions.add(new Dimension(30, 90));
		dimensions.add(new Dimension(20, 0));

		assertTrue(CutPlanner.widthAtPosition(dimensions, 0) == 30);
		assertTrue(CutPlanner.widthAtPosition(dimensions, 45) == 25);
		assertTrue(CutPlanner.widthAtPosition(dimensions, 90) == 20);
		assertTrue(CutPlanner.widthAtPosition(dimensions, 100) == 0);
		
		dimensions.clear();
		dimensions.add(new Dimension(40, 10));
		dimensions.add(new Dimension(30, 10)); 
		dimensions.add(new Dimension(20, 10));
		dimensions.add(new Dimension(8, 0));
		
		assertTrue(CutPlanner.widthAtPosition(dimensions, 5) == 35);
		assertTrue(CutPlanner.widthAtPosition(dimensions, 12) == 29);
		assertTrue(CutPlanner.widthAtPosition(dimensions, 28) == 9);
		assertTrue(CutPlanner.widthAtPosition(dimensions, 100) == 0);
		
	}
}
