package com.bitflippersanonymous.buck.domain;

import java.util.List;

import com.bitflippersanonymous.buck.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


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
					R.layout.picker_entry, parent, false);

		TextView tc = (TextView)convertView.findViewById(R.id.textViewPickerCuts);
		tc.setText(makeCutString(plan.getCuts()));
		TextView tv = (TextView)convertView.findViewById(R.id.textViewPickerValue);
		tv.setText(Integer.valueOf(plan.getBoardFeet()).toString());
		return convertView;
	}

	private String makeCutString(List<Integer> cuts) {
		String cutString = new String();
		if ( cuts.size() == 0 ) {
			cutString = "Ship It"; // FIXME: get from R.strings
		} else {
			for ( int i = 0 ; i < cuts.size(); i++) {
				cutString +=  cuts.get(i).toString();
				if ( i < cuts.size() - 1 ) cutString += ", ";
			}
		}
		return cutString;
	}
}
