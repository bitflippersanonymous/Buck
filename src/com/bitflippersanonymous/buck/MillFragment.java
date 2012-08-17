package com.bitflippersanonymous.buck;



import com.bitflippersanonymous.buck.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

class MillFragment extends Fragment {
	public MillFragment() {	}
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                            Bundle savedInstanceState) {
	    return(inflater.inflate(R.layout.fragment_mill, container, false));
	  }
}