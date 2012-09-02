package com.bitflippersanonymous.buck.ui;

import java.util.ArrayList;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Cut;
import com.bitflippersanonymous.buck.domain.Util;

import android.os.Bundle;
import android.widget.TextView;


public class CutOptionsActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cutoptions);
		
		ArrayList<Cut> cuts = getIntent().getParcelableArrayListExtra(Util.CUTS);
			
		/*
		Generate a ListView with the dollar values of each cut option.  Means we will recalculate 
		each time activity is created.  Need to do this an an async_task (Or a loader?)
		*/
		TextView bF = (TextView) findViewById(R.id.textViewBF);
		bF.setText(Integer.toString(getService().getBoardFeet(cuts.get(0))));
	}
	
	
	
}
