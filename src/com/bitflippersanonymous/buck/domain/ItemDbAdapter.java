package com.bitflippersanonymous.buck.domain;


import com.bitflippersanonymous.buck.domain.Mill;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.support.v4.widget.*;


public class ItemDbAdapter extends CursorAdapter implements ListAdapter {
	public ItemDbAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Mill mill = Mill.cursorToItem(cursor);
	    view.setTag(mill);
        TextView name = (TextView) view.findViewById(android.R.id.text1);
        name.setText(mill.get(Mill.Tags.Name));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
	    View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, true);
		bindView(view, context, cursor);
        return view;
	}
}