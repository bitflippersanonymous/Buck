package com.bitflippersanonymous.buck.domain;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


// TODO: Not all of these overrides are needed
// Maybe store array here and not extend ArrayAdapter
public class PickerAdapter extends ArrayAdapter<CutPlan> implements ListAdapter {

	public PickerAdapter(Context context, int textViewResourceId,
			List<CutPlan> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CutPlan plan = getItem(position);
		if ( convertView == null )
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					android.R.layout.simple_list_item_1, parent, false);
		TextView t1 = (TextView)convertView.findViewById(android.R.id.text1);
		t1.setText(Integer.valueOf(plan.getBoardFeet()).toString());
		return convertView;
	}
}
