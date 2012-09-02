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
		ListView list = (ListView) findViewById(R.id.listViewBuck);
		if ( mAdapter == null) {
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
		  		convertView = LayoutInflater.from(getContext()).inflate(R.layout.cut_entry, parent, false);
		  	Cut cut = getItem(position);
		  	TextView tv = ((TextView)convertView.findViewById(R.id.textViewWidth));
		  	tv.setText(String.valueOf(cut.getWidth()));
		  	tv = ((TextView)convertView.findViewById(R.id.textViewLength));
		  	tv.setText(String.valueOf(cut.getLength()));
		  	View removeButton = convertView.findViewById(R.id.imageViewRemove);
		  	removeButton.setTag(position);
		  	removeButton.setOnClickListener(BuckActivity.this);
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
			int width = Integer.parseInt(wedit.getText().toString());
			EditText ledit = (EditText)findViewById(R.id.editTextBuckLength);
			int length = Integer.parseInt(ledit.getText().toString());
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
		case R.id.imageViewRemove:
			mAdapter.remove(mAdapter.getItem((Integer)v.getTag()));
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
			intent.putParcelableArrayListExtra(Util.CUTS, mCuts);
			startActivity(intent);
			// May not want to clear here, wait until piece is cut and added
			clearTextEdits();
			mCuts.clear();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
