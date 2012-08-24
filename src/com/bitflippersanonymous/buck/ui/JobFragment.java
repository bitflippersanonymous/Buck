package com.bitflippersanonymous.buck.ui;



import com.bitflippersanonymous.buck.R;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JobFragment extends Fragment {
	public JobFragment() {	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return(inflater.inflate(R.layout.fragment_job, container, false));
	}
}