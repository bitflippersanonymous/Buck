package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.service.BuckService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public abstract class BuckBaseActivity extends FragmentActivity
	implements ServiceConnection {

	private boolean mBound = false;
	private static BuckService mService = null;
	
	protected boolean isBound() {
		return mBound;
	}

	public static BuckService getService() { 
		//TODO: If service not bound, bind it and wait until it's bound, then return it
		while ( mService == null || mService.getDbAdapter() == null ) {
			try { Thread.sleep(1000); } catch(InterruptedException e){ }
			if ( mService == null )
				Log.e(BuckBaseActivity.class.getName(), "Service not started");
			if ( mService.getDbAdapter() == null )
				Log.e(BuckBaseActivity.class.getName(), "DB not started");
		}
		return mService; 
	}

    private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			update();
		}
	};
	
	protected Handler getHandler() {
		return mHandler;
	}
	
	private Messenger mMessenger = new Messenger(getHandler());

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		BuckService.LocalBinder binder = (BuckService.LocalBinder) service;
		mService = binder.getService();
		mService.addClient(mMessenger);
		mBound = true;
		update();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mService = null;
		mBound = false;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.i(getClass().getSimpleName(), "onStop");
	    if ( mBound ) {
	    	getService().removeClient(mMessenger);
	        unbindService(this);
	    }
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(getClass().getSimpleName(), "onStart");
		Intent intent = new Intent(this, BuckService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}
	
	protected void update() {
	}
	


}