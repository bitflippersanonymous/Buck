package com.bitflippersanonymous.buck;



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

class MainListFragment extends ListFragment {
	public interface OnItemListener {
		void onItemSelected(Object item);
	}

	public static final String ARG_SECTION_NUMBER = "SECTION_NUMBER";
	private OnItemListener mListener = null;
	
	public MainListFragment() {	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if ( mListener != null ) {
			mListener.onItemSelected(getListAdapter().getItem(position));
		}
	}
	
	public void setOnItemListener(OnItemListener listener) {
		mListener = listener;
	}
}