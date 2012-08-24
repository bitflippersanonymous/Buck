package com.bitflippersanonymous.buck.ui;

import java.util.ArrayList;
import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Cut;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewGroup;

public class BuckActivity extends BaseActivity implements View.OnClickListener {
	
	private ArrayAdapter<Cut> mAdapter = null;
	private ArrayList<Cut> mCuts = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buck);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_activity_buck);
		
		for (int id : new int[]{R.id.buttonBuckClear, R.id.buttonBuckAdd} )
			findViewById(id).setOnClickListener(this);
	
		/*
		 * An adapter that holds the data for each measurement to be displayed in the ListView
		 * Has an override to map a Cut to a View
		 */
		mAdapter = new ArrayAdapter<Cut>(this, 0, 0, mCuts = new ArrayList<Cut>()) {
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		  	if ( convertView == null )
		  		convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
		  	Cut cut = getItem(position);
		  	TextView tv = ((TextView)convertView.findViewById(android.R.id.text1));
		  	tv.setText(String.valueOf(cut.getWidth()));
		  	tv = ((TextView)convertView.findViewById(android.R.id.text2));
		  	tv.setText(String.valueOf(cut.getLength()));
		    return convertView;
		  }
		};
		
		ListView list = (ListView) findViewById(R.id.listViewBuck);
		list.setAdapter(mAdapter);
	}

	// FIXME: save/restore the whole mAdapter array
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(Util.CUTS)) {
			mCuts = savedInstanceState.getParcelableArrayList(Util.CUTS);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(Util.CUTS, mCuts);
	}
	
	//FIXME: Only show 'finish' menu option when mCuts.length() > 0
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_buck, menu);
		return true;
	}

	
	private void clearTextEdits() {
		for (int id : new int[]{R.id.editTextBuckLength, R.id.editTextBuckWidth} )
			((EditText)findViewById(id)).getText().clear();
		findViewById(R.id.editTextBuckWidth).requestFocus();
	}
	
	private void addCut() {
		Cut cut = new Cut();
		try { 
			EditText wedit = (EditText)findViewById(R.id.editTextBuckWidth);
			cut.setWidth(Float.parseFloat(wedit.getText().toString()));
			EditText ledit = (EditText)findViewById(R.id.editTextBuckLength);
			cut.setLength(Float.parseFloat(ledit.getText().toString()));
		} catch (NumberFormatException e) {
			return;
		}
		mAdapter.add(cut);
	}
	
	@Override
	public void onClick(View v) {
		switch ( v.getId() ) {
		case R.id.buttonBuckClear:
			clearTextEdits();
			break;
		case R.id.buttonBuckAdd:
			addCut();
			clearTextEdits();
			break;
		default:
		}		
		
	}

	
	/**
	 * Handles option item selections, including 'Home', which restarts the main activity
	 * resetting your current location to default Mills. Buck needs a home landing activity
	 * instead
	 * Move this to BaseActivity
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
		case R.id.menu_done:
			Intent intent = new Intent(this, CutOptionsActivity.class);
			// FIXME: how to put mCuts in correctly?
			intent.putExtra("", mCuts);
			//Bundle args = new Bundle();
			//intent.getExtras().putParcelableArrayList(Util.CUTS, mCuts);
			startActivity(intent);
			clearTextEdits();
			mCuts.clear();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
