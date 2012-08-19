package com.bitflippersanonymous.buck.ui;

 
// Comment
import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Mill;
import com.bitflippersanonymous.buck.domain.Mill.Tags;
import com.bitflippersanonymous.buck.domain.Util;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class MillFragment extends Fragment {
	public MillFragment() {	}
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                            Bundle savedInstanceState) {
		  int rowId = getActivity().getIntent().getExtras().getInt(Util._ID);
		  String table = Util.DatabaseBase.Tables.Mills.name();
		  Cursor cursor = BaseActivity.getService().getDbAdapter().fetchEntry(table, rowId);
		  Mill mill = Mill.cursorToItem(cursor);
		  
		  View view = inflater.inflate(R.layout.fragment_mill, container, false);
		  TextView t = (TextView)view.findViewById(R.id.textView1);
		  t.setText(mill.get(Tags.Name));
		  return view;
	  }
}