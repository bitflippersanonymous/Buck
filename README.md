Buck
============
Timber Bucking program for Android
Copyright 2012 Ryan Henderson All rights reserved.


Notes:
	Use this to storage location for temp downloaded files
	getCacheDir();

		
	Turn on Strict Mode for debug
	onCreate() {
	if (BuildConfig.DEBUG
		&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		StrictMode.setThreadPolicy(buildPolicy());
		}
	}
	private StrictMode.ThreadPolicy buildPolicy() {
		return(new StrictMode.ThreadPolicy.Builder().detectAll()
			.penaltyLog().build());
	}
	
	
	Close ListAdapter cursor on activity/fragment destroy. Thought this was done for you.
	@Override
	public void onDestroy() {
		super.onDestroy();
		((CursorAdapter)getListAdapter()).getCursor().close();
	}
	
	
	The basic recipe for your own transactions is:
	try {
		db.beginTransaction();
		// several SQL statements in here
		db.setTransactionSuccessful();
	}
	finally {
		db.endTransaction();
	}