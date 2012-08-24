package com.bitflippersanonymous.buck.domain;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class JobDbAdapter extends CursorAdapter implements ListAdapter {
	public JobDbAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Job job = Job.cursorToItem(cursor);
		TextView name = (TextView) view.findViewById(android.R.id.text1);
		name.setText(job.get(Job.getTags()[0]));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
		bindView(view, context, cursor);
		return view;
	}
}