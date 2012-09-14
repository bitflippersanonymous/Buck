package com.bitflippersanonymous.buck.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bitflippersanonymous.buck.db.BuckDatabaseAdapter;
import com.bitflippersanonymous.buck.domain.Cut;
import com.bitflippersanonymous.buck.domain.CutPlan;
import com.bitflippersanonymous.buck.domain.DBItemXMLReader;
import com.bitflippersanonymous.buck.domain.DbItem;
import com.bitflippersanonymous.buck.domain.Job;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Price;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Util.DatabaseBase.Tables;
import com.bitflippersanonymous.buck.ui.BaseActivity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class BuckService extends Service  {
	private final IBinder mBinder = new LocalBinder();
	final private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	private BuckDatabaseAdapter mDbAdapter = null;
	private LoadTask mLoadTask = null;
	private Map<Cut, Integer> mScribnerTable = null;
	
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
		
		loadScribner();
		
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
	private boolean loadScribner() {
		if ( mLoadTask != null )
			return false;

		mScribnerTable = new HashMap<Cut, Integer>();
		Util.FileReader reader = new ScribnerReader(mScribnerTable, "scribner.csv");
		(mLoadTask = new LoadTask()).execute(reader);
		return true;
	}

	class ScribnerReader implements Util.FileReader {
		List<Integer> mWidths;
		Map<Cut, Integer> mScribnerTable;
		String mFilename;
		
		public ScribnerReader(Map<Cut, Integer> scribnerTable, String filename) {
			mScribnerTable = scribnerTable;
			mFilename = filename;
		}
		
		@Override
		public void handleLine(String line) {
			List<Integer> ints = new ArrayList<Integer>();
			 for ( String s : line.split(","))
				 ints.add(Integer.parseInt(s));
			 if ( mWidths == null )
				 mWidths = ints;
			 else {
				 // FIXME: Check for nulls here, table may be incomplete
				 for ( int i = 1; i < ints.size(); i++ )
					 mScribnerTable.put(new Cut(mWidths.get(i), ints.get(0)), ints.get(i));
			 }
		}

		@Override
		public String getFilename() {
			return mFilename;
		}

	}
		

	// TODO: Need to call some sort of finish when this is done to make the task thread go away
	// TODO: As is, hard to tell if table load is complete
	class LoadTask extends AsyncTask<Util.FileReader, Void, Integer> {
		// Could put file in raw, then could access with R.id instead of filename
		// Could put this into a class, then subclass that for handler
		public void readFile(Util.FileReader reader) {
			try {
				AssetManager am = getAssets();
				InputStream instream = am.open(reader.getFilename());
				if ( instream != null ) {
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					while (( line = buffreader.readLine()) != null) {
						reader.handleLine(line);
					}
				}
				instream.close();
			} catch (java.io.FileNotFoundException e) {
				Log.e(getClass().getSimpleName(), "File not found: " + reader.getFilename());
			} catch (IOException e) {
				Log.e(getClass().getSimpleName(), "Error reading: " + reader.getFilename());
			}
		}

		@Override
		protected Integer doInBackground(Util.FileReader... params) {
			readFile(params[0]);
			return 0;
		}

		@Override
		protected void onCancelled() {
			mLoadTask = null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i(getClass().getSimpleName(), "Load Complete");
			mLoadTask = null;
		}
	}
	

	
	//FIXME: mLoadTask may not be complete yet.
	public int getBoardFeet(Cut cut) {
		Cut trim = new Cut(cut.getWidth(), cut.getLength());
		Integer bf = mScribnerTable.get(trim);
		if ( bf != null )
			return bf;
		
		Log.e(getClass().getSimpleName(), "Scribner Table Incomplete: " + cut);
		return 0;
	}

	public List<CutPlan> getCutPlans(List<Cut> cuts) {
		List<CutPlan> plans = new ArrayList<CutPlan>();
		Mill mill = getMill(1);
		
		for ( Price price : mill.getPrices() ) {
			int length = price.getAsInteger(Price.Fields.Length);
			
		}
		
		CutPlan plan = new CutPlan(cuts);
		plans.add(plan);
		
		int boardFeet = 0;
		List<Integer> ints = new ArrayList<Integer>();
		for ( Cut cut : cuts ) {
			boardFeet += getBoardFeet(cut);
			ints.add(cut.getLength());
		}
		plan.setCuts(ints);
		plan.setBoardFeet(boardFeet);
		return plans;
	}

	public void savePick(CutPlan item) {
		Log.e(getClass().getSimpleName(), "Store CutPlan in db");
	}

	public Mill getMill(int millId) {
		Cursor cursor = getDbAdapter().fetchEntry(Tables.Mills, millId);
		Mill mill = new Mill(cursor);
		mill.setPrices(getPrices(millId));
		return mill;
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


}