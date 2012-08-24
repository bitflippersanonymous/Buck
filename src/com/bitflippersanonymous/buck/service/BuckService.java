package com.bitflippersanonymous.buck.service;


import java.util.ArrayList;
import java.util.HashMap;

import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class BuckService extends Service  {
	private final IBinder mBinder = new LocalBinder();
	final private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	private BuckDatabaseAdapter mDbAdapter = new BuckDatabaseAdapter(this);
	private LoadTask mLoadTask = null;
	
	public BuckDatabaseAdapter getDbAdapter() {
		return mDbAdapter;
	}
	
	public class LocalBinder extends Binder {
		public BuckService getService() {
			return BuckService.this;
		}
	}

	public void onCreate() {
		//TODO: Need to load database on service create if it is empty
		// Load with sample test data?
		mDbAdapter.recreate();
		final String mills[] = {"Big Lumber", "LP", "Boise"};
		for ( String mill : mills ) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put(Mill.getTags()[0], mill);
			mDbAdapter.insertItem(new Mill(data, -1));
		}

		final String jobs[] = {"Back 40", "Homeplace"};
		for ( String job : jobs ) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put(Job.getTags()[0], job);
			mDbAdapter.insertItem(new Job(data, -1));
		}
		
		
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void addClient(Messenger messenger) {
	if ( messenger != null )
		mClients.add(messenger);
	}
		
	public void removeClient(Messenger messenger) {
		mClients.remove(messenger);
	}
	
	@Override
	public void onDestroy() {
		if ( mDbAdapter != null ) {
			mDbAdapter.close();
			mDbAdapter = null;
		}
		Log.i(getClass().getSimpleName(), "Destroyed");
	}

	public void sendUpdate() {
		try {
			for ( Messenger messenger : mClients ) {
				Message msg = Message.obtain();
				messenger.send(msg);
			}
		}
		catch (android.os.RemoteException e) {
			Log.w(getClass().getName(), "Exception sending message", e);
		}
	}
	
	// Used to read XML data into database in background.
	public boolean refreshDb() {
		if ( mLoadTask != null )
			return false;
		String path = null;
		mLoadTask = new LoadTask();
		mLoadTask.execute(path);
		return true;
	}

	// TODO: Need to call some sort of finish when this is done to make the task thread go away
	// Used by refreshDb
	class LoadTask extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			//String path = params[0];
			// Do something that takes long time
			// XML -> db
			return 0;
		}

		@Override
		protected void onCancelled() {
			// Should we close db here?
			mLoadTask = null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i(getClass().getSimpleName(), "Load Complete");
			mLoadTask = null;
			sendUpdate();
		}

		@Override
		protected void onPreExecute() {
		}
	}


}
