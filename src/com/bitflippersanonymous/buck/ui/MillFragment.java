package com.bitflippersanonymous.buck.ui;


// Comment
import com.bitflippersanonymous.buck.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class MillFragment extends Fragment {
	public MillFragment() {	}
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                            Bundle savedInstanceState) {
	    return(inflater.inflate(R.layout.fragment_mill, container, false));
	  }
}