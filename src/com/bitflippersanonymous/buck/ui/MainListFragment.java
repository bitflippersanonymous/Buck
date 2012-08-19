package com.bitflippersanonymous.buck.ui;



import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

class MainListFragment extends ListFragment {
	public interface OnItemListener {
		void onItemSelected(Object item);
	}

	public static final String ARG_SECTION_NUMBER = "SECTION_NUMBER";
	private OnItemListener mListener = null;
	
	public MainListFragment() {	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		super.onListItemClick(l, view, position, id);

		if ( mListener != null ) {
			mListener.onItemSelected(getListAdapter().getItem(position));
		}
	}
	
	public void setOnItemListener(OnItemListener listener) {
		mListener = listener;
	}
}