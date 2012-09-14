package com.bitflippersanonymous.buck.domain;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

// FIXME: Have a leak here if service is restarted while task is running
public class DBItemXMLReader {
	
	//FIXME: Only need context to get started
	private Context mContext;
	private LoadXmlTask mTask = null;
	private Util.InsertItems mInsertItems = null;

	public DBItemXMLReader(Context context, Util.InsertItems insertItems) {
		mContext = context;
		mInsertItems = insertItems;
	}
	
	/**
	 * @param filename
	 * @return AsyncTask that was started
	 */
	public LoadXmlTask loadXML(String filename) {
		// Already busy
		if ( mTask != null )
			return null;
		
		AssetManager am = mContext.getAssets();
		InputStream instream;
		try {
			instream = am.open(filename);
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Error reading: " + filename);
			return null;
		}
		(mTask = new LoadXmlTask()).execute(instream, new XMLHandler());
		return mTask;
	}

	public void onPostExecute(Integer result) {
	}
	
	private class XMLHandler extends DefaultHandler {
	    private DbItem<?> mCurrent = null;
		private Mill mMill = null;
	    
		@Override
	    public void endElement(String uri, String localName, String name)
	            throws SAXException {
	        super.endElement(uri, localName, name);

	        if ( localName.equals(Mill.MILL) ) {
	        	DBItemXMLReader.this.mInsertItems.insertMill(mMill);
	        	mMill = null;
	        }
		}

	    @Override
	    public void startElement(String uri, String localName, String name, Attributes attributes) 
	    		throws SAXException {
	        super.startElement(uri, localName, name, attributes);
	        
	        // Just pass attributes to c'tor and let it fill in?
	        if ( localName.equals(Mill.MILL) ) {
	        	mCurrent = mMill = new Mill(-1);
	        } else if ( localName.equals(Price.PRICE) && mMill != null ) {
	        	Price price = new Price(-1);
	        	mMill.getPrices().add(price);
	        	mCurrent = price;
	        } else if ( localName.equals(Job.JOB) )
	        	mCurrent = new Job(-1);
	        else {
				Log.w(getClass().getName(), "Unhandled XML Element: " + localName );
				return;
	        }
	        for ( int i = 0; i < attributes.getLength(); i++ ) {
	        	mCurrent.put(attributes.getLocalName(i), attributes.getValue(i));
	        }
	    }
	}
	
	private class SAXExceptionEnough extends SAXException {
		private static final long serialVersionUID = 240553706960958221L;
	};
	
	private void parseXML(InputStream inputStream, DefaultHandler handler) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			if ( inputStream != null )
				parser.parse(inputStream, handler);
		} catch (SAXExceptionEnough e) {

		} catch (Exception e) {
			Log.w(getClass().getName(), "Exception reading stream", e);
		}

		if ( inputStream != null ) {
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.w(getClass().getName(), "Exception reading stream", e);
			}
		}
	}	

	// FIXME: Can I, Do I want to pass the stream across threads?
	private class LoadXmlTask extends AsyncTask<Object, Integer, Integer> {
		@Override
		protected Integer doInBackground(Object... params) {
			parseXML((InputStream)params[0], (DefaultHandler)params[1]);
			return 0;
		}

		@Override
		protected void onCancelled() {
			// set transaction bad and end transaction
			mTask = null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onPostExecute(Integer result) {
			// End Transaction
			mTask = null;
			DBItemXMLReader.this.onPostExecute(result);
			Log.i(getClass().getSimpleName(), "XML Load Complete");
		}

		@Override
		protected void onPreExecute() {
			// begin_transaction
			// Can this even happen?
			if ( mTask != this ) {
				Log.i(getClass().getSimpleName(), "XML Load Wrong Task");
				this.cancel(true);
			}
		}
	}

}
