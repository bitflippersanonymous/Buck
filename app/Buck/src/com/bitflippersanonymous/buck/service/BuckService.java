package com.bitflippersanonymous.buck.service;


import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.CutNode;
import com.bitflippersanonymous.buck.domain.CutPlanner;
import com.bitflippersanonymous.buck.domain.Dimension;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Price;
import com.bitflippersanonymous.buck.domain.JobSummary;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.ui.BaseActivity;
import com.bitflippersanonymous.buck.ui.SettingsActivity;

public class BuckService extends Service  {
	private final IBinder mBinder = new LocalBinder();
	final private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	private BuckDatabaseAdapter mDbAdapter = null;
	private CutPlanner mCutPlanner = null;

	public BuckDatabaseAdapter getDbAdapter() {
		return mDbAdapter;
	}
	
	public class LocalBinder extends Binder {
		public BuckService getService() {
			return BuckService.this;
		}
	}

	//FIXME: Rotating the phone while at the top Activity will recreate the service
	//  		  ...and all of this will run again
	public void onCreate() {
		mDbAdapter = new BuckDatabaseAdapter(this);
		
		//TODO: Load from xml
		mDbAdapter.recreate();

		final String jobs[] = {"Back 40", "Homeplace"};
		for ( String name : jobs ) {
			Job job = new Job(-1);
			job.put(Job.Fields.Name, name);
			mDbAdapter.insertItem(job);
		}
		
		mCutPlanner = new CutPlanner(this, Util.SCRIBNER);
		
		// Don't reuse reader until it's done, create new one for each file
		new DBItemXMLReader(this, mDbAdapter){
			@Override
			public void onPostExecute(Integer result) {
				sendUpdate();
			}
		}.loadXML("database_init.xml");
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Can we stall here until we're loaded?
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
	
	public void addPieceToJob(CutNode item, Job job) {
		getDbAdapter().addPieceToJob(item, job);
		Log.e(getClass().getSimpleName(), "Store CutPlan in db");
	}

	public Mill getMill(int millId) {
		Cursor cursor = getDbAdapter().fetchEntry(Tables.Mills, millId);
		Mill mill = new Mill(cursor);
		mill.setPrices(getPrices(millId));
		return mill;
	}

	public Job getJob(int jobId) {
		Cursor cursor = getDbAdapter().fetchEntry(Tables.Jobs, jobId);
		Job job = new Job(cursor);
		job.setSummary(getSummary(jobId));
		return job;
	}

	
	private List<JobSummary> getSummary(int jobId) {
		List<JobSummary> summary = new ArrayList<JobSummary>();
		return summary;
	}

	private List<Price> getPrices(int millId) {
		List<Price > prices = new ArrayList<Price>();
		Cursor cursor =  getDbAdapter().fetchPrices(millId);
		for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
			prices.add(new Price(cursor));
		}
		cursor.close();
		return prices;
	}

	public List<CutNode> getCutPlans(List<Dimension> dimensions, int millId) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int kerf = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_KERF, "0"));
		int minTop = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_TOP, "0"));
		if ( kerf <= 0 || minTop <= 0 )
			Log.e(getClass().getSimpleName(), "error getting kerf, minTop in getCutPlans");
		return mCutPlanner.getCutPlans(getMill(millId), dimensions, kerf, minTop); 
	}
	
	public int getBoardFeet(Dimension dim) {
		return mCutPlanner.getBoardFeet(dim);
	}

	public void setMillEnabled(int rowId, boolean checked) {
		ContentValues values = new ContentValues();
		values.put(Mill.Fields.Enabled.name(), checked ? "1" : "0");
		getDbAdapter().updateTable(Tables.Mills.name(), rowId, values);
		sendUpdate();
	}


}
