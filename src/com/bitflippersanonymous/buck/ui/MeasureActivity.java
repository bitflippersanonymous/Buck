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

public class MeasureActivity extends BaseActivity implements View.OnClickListener {
	
	private ArrayAdapter<Cut> mAdapter = null;
	private ArrayList<Cut> mCuts = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measure);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_activity_measure);
		
		for (int id : new int[]{R.id.buttonMeasureClear, R.id.buttonMeasureAdd} )
			findViewById(id).setOnClickListener(this);
	
		//Restore instance state can set mAdapter
		ListView list = (ListView) findViewById(R.id.listViewMeasure);
		if ( mAdapter == null) {
			list.setAdapter(mAdapter = makeAdapter(mCuts = new ArrayList<Cut>()));
		}

		EditText editText = (EditText) findViewById(R.id.editTextMeasureLength);
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
		  		convertView = LayoutInflater.from(getContext()).inflate(R.layout.measure_entry, parent, false);
		  	Cut cut = getItem(position);
		  	TextView tv = ((TextView)convertView.findViewById(R.id.textViewWidth));
		  	tv.setText(String.valueOf(cut.getWidth()));
		  	tv = ((TextView)convertView.findViewById(R.id.textViewLength));
		  	tv.setText(String.valueOf(cut.getLength()));
		  	View removeButton = convertView.findViewById(R.id.imageViewRemove);
		  	removeButton.setTag(position);
		  	removeButton.setOnClickListener(MeasureActivity.this);
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
			ListView list = (ListView) findViewById(R.id.listViewMeasure);
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
		getMenuInflater().inflate(R.menu.activity_measure, menu);
		return true;
	}

	
	private void clearTextEdits() {
		for (int id : new int[]{R.id.editTextMeasureLength, R.id.editTextMeasureWidth} )
			((EditText)findViewById(id)).getText().clear();
		findViewById(R.id.editTextMeasureWidth).requestFocus();
	}
	
	private void addCut() {
		try { 
			EditText wedit = (EditText)findViewById(R.id.editTextMeasureWidth);
			int width = Integer.parseInt(wedit.getText().toString());
			EditText ledit = (EditText)findViewById(R.id.editTextMeasureLength);
			int length = Integer.parseInt(ledit.getText().toString());
			mAdapter.add(new Cut(width, length));
			ListView list = (ListView) findViewById(R.id.listViewMeasure);
			list.setSelectionFromTop(mAdapter.getCount(), 0);
		} catch (NumberFormatException e) {
			return;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch ( v.getId() ) {
		case R.id.buttonMeasureClear:
			clearTextEdits();
			break;
		case R.id.buttonMeasureAdd:
			addCut();
			clearTextEdits();
			break;
		case R.id.imageViewRemove:
			mAdapter.remove(mAdapter.getItem((Integer)v.getTag()));
		default:
		}		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_done:
			Intent intent = new Intent(this, CutActivity.class);
			intent.putParcelableArrayListExtra(Util.CUTS, mCuts);
			startActivity(intent);
			clearTextEdits();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
