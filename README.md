Buck
============
Timber Bucking program for Android
Copyright 2012 Ryan Henderson All rights reserved.

Branch: develop

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

	RenamingDelegatingContext 
	provides a Context in which most functions are handled by an existing Context, but file and database operations are handled by a IsolatedContext. The isolated part uses a test directory and creates special file and directory names. You can control the naming yourself, or let the constructor determine it automatically.

