package com.bitflippersanonymous.buck;
import java.io.IOException;
import java.util.ArrayList;

import com.bitflippersanonymous.flippy.R;
import com.bitflippersanonymous.flippy.activity.FlippyInfoActivity;
import com.bitflippersanonymous.flippy.db.FlippyDatabaseAdapter;
import com.bitflippersanonymous.flippy.domain.*;
import com.bitflippersanonymous.flippy.domain.PlsEntry.Tags;
import com.bitflippersanonymous.flippy.util.*;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.BaseAdapter;

public class BuckService extends Service  {
	private final IBinder mBinder = new LocalBinder();
	final private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	private FlippyDatabaseAdapter mDbAdapter = new FlippyDatabaseAdapter(this);;
	private LoadTask mLoadTask = null;
	
	public FlippyDatabaseAdapter getDbAdapter() {
		return mDbAdapter;
	}
	
	public class LocalBinder extends Binder {
		public BuckService getService() {
			return BuckService.this;
		}
	}

	public void onCreate() {
		//TODO: Need to load database on service create if it is empty
		//refreshDb();
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
	
	public boolean refreshDb() {
		if ( mLoadTask != null )
			return false;
		
		String path = getResources().getString(R.string.recent_messages_url);
		
		mLoadTask = new LoadTask();
		mLoadTask.execute(path);
		return true;
	}

	// TODO: Need to call some sort of finish when this is done to make the task thread go away
	class LoadTask extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			String path = params[0];
			PodcastParser parser = new PodcastParser(path, mDbAdapter);
			parser.parse();
		
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
			mLoadComplete  = true;
			mLoadTask = null;
			sendUpdate();
		}

		@Override
		protected void onPreExecute() {
			mLoadComplete = false;
		}
	}


}
