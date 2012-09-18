package com.bitflippersanonymous.buck.domain;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

// Almost simple enough to do away with and use SimpleCursorAdapter
public class MillDbAdapter extends CursorAdapter implements ListAdapter {
	public MillDbAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(Mill.Fields.Name.name());
		String millName = cursor.getString(columnIndex);
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(millName);
		int drawable = 0;
		if ( cursor.getInt(cursor.getColumnIndex(Mill.Fields.Enabled.name())) > 0 )
			drawable = android.R.drawable.star_on;
		textView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				android.R.layout.simple_list_item_1, parent, false);
		bindView(view, context, cursor);
		return view;
	}
}