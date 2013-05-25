package com.bitflippersanonymous.buck.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.service.BuckService;

/**
 * 
 * Base class for activities that need access to the service.  The service
 * maintains a persistent database connection as activities are created
 * and destroyed.
 */
public abstract class BaseActivity extends Activity	implements ServiceConnection {
	
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
				Log.e(BaseActivity.class.getName(), "Service not started");
			if ( mService.getDbAdapter() == null )
				Log.e(BaseActivity.class.getName(), "DB not started");
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
	public void onDestroy() {
		super.onDestroy();
		Log.i(getClass().getSimpleName(), "onDestroy");
		if ( mBound ) {
			getService().removeClient(mMessenger);
			unbindService(this);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getClass().getSimpleName(), "onCreate");
		Intent intent = new Intent(this, BuckService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent.addFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TOP |
					Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/* 
	 * Currently unused. 
	 * Was used for the service to notify connected activities 
	 * to update their views */
	protected void update() {
	}
}