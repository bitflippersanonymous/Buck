package com.bitflippersanonymous.buck.ui;

import java.util.ArrayList;
import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Util;
import com.bitflippersanonymous.buck.domain.Cut;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

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
	
		//Restore instance state can set mAdapter
		if ( mAdapter == null) {
			ListView list = (ListView) findViewById(R.id.listViewBuck);
			list.setAdapter(mAdapter = makeAdapter(mCuts = new ArrayList<Cut>()));
		}

		EditText editText = (EditText) findViewById(R.id.editTextBuckLength);
		editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addCut();
					clearTextEdits();
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * An adapter that holds the data for each measurement to be displayed in the ListView
	 * Has an override to map a Cut to a View
	 */
	private ArrayAdapter<Cut> makeAdapter(ArrayList<Cut> cuts) {
		ArrayAdapter<Cut> adapter = new ArrayAdapter<Cut>(this, 0, 0, cuts) {
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
		return adapter;
	}

	// FIXME: Why rebuild whole adapter instead of just changing data under it
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(Util.CUTS)) {
			mCuts = savedInstanceState.getParcelableArrayList(Util.CUTS);
			ListView list = (ListView) findViewById(R.id.listViewBuck);
			list.setAdapter(mAdapter = makeAdapter(mCuts));
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
		try { 
			EditText wedit = (EditText)findViewById(R.id.editTextBuckWidth);
			float width = Float.parseFloat(wedit.getText().toString());
			EditText ledit = (EditText)findViewById(R.id.editTextBuckLength);
			float length = Float.parseFloat(ledit.getText().toString());
			mAdapter.add(new Cut(width, length));
			ListView list = (ListView) findViewById(R.id.listViewBuck);
			list.setSelectionFromTop(mAdapter.getCount(), 0);
		} catch (NumberFormatException e) {
			return;
		}
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
