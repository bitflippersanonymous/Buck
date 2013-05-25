package com.bitflippersanonymous.buck.ui;



import com.bitflippersanonymous.buck.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

class MainListFragment extends ListFragment {
	public interface OnItemListener {
		void onItemSelected(Object item, View view);
	}

	public static final String ARG_SECTION_NUMBER = "SECTION_NUMBER";
	private OnItemListener mListener = null;
	
	public MainListFragment() {	}

    @Override 
    public  void  onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
    }
	
	/**
	 * Notify our listener of list item clicks passing the list item
	 * instead of the position.
	 */
	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		super.onListItemClick(l, view, position, id);
		if ( mListener != null ) {
			mListener.onItemSelected(getListAdapter().getItem(position), view);
		}
	}
	
	public void setOnItemListener(OnItemListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Toast.makeText(this.getActivity(), "foo", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}