package com.bitflippersanonymous.buck.test;

import com.bitflippersanonymous.buck.domain.Cut;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.service.BuckService;

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
		Cut cut = new Cut(0, 0);
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
}
