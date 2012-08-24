package com.bitflippersanonymous.buck.ui;

import java.util.LinkedList;
import java.util.List;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.ui.BuckActivity.Cut;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewGroup;

public class BuckActivity extends BaseActivity implements View.OnClickListener {

	// Needs to preserve through Create/Destroy
	class Cut {
		float mWidth;
		float mLength;
	}
	
	private ArrayAdapter<Cut> mAdapter;
	
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
		mAdapter = new ArrayAdapter<Cut>(this, 0, 0, new LinkedList<Cut>()) {
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		  	if ( convertView == null )
		  		convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
		  	Cut cut = getItem(position);
		  	TextView tv = ((TextView)convertView.findViewById(android.R.id.text1));
		  	tv.setText(String.valueOf(cut.mWidth));
		  	tv = ((TextView)convertView.findViewById(android.R.id.text2));
		  	tv.setText(String.valueOf(cut.mLength));
		    return convertView;
		  }
		};
		
		ListView list = (ListView) findViewById(R.id.listViewBuck);
		list.setAdapter(mAdapter);
	}

	// FIXME: save/restore the whole mAdapter array
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey("Foo")) {
			int foo = savedInstanceState.getInt("Foo");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("Foo", 0);
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
			cut.mWidth = Float.parseFloat(wedit.getText().toString());
			EditText ledit = (EditText)findViewById(R.id.editTextBuckLength);
			cut.mLength = Float.parseFloat(ledit.getText().toString());
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
		}
		return super.onOptionsItemSelected(item);
	}
}
