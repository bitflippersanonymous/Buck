package com.bitflippersanonymous.buck.domain;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bitflippersanonymous.buck.R;


public class CutAdapter extends ArrayAdapter<CutNode> implements ListAdapter {

	public CutAdapter(Context context, int textViewResourceId, List<CutNode> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CutNode node = getItem(position);
		if ( convertView == null )
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.cut_entry, parent, false);

		TextView tc = (TextView)convertView.findViewById(R.id.textViewCutCuts);
		tc.setText(makeCutString(node.getTotalCuts()));
		TextView tv = (TextView)convertView.findViewById(R.id.textViewCutValue);
		tv.setText("$"+Integer.valueOf(node.getTotalValue()).toString());
		return convertView;
	}

	private String makeCutString(List<Dimension> cuts) {
		String cutString = new String();
		if ( cuts.size() == 0 ) {
			cutString = "Ship It"; // FIXME: get from R.strings
		} else {
			for ( int i = 0 ; i < cuts.size(); i++) {
				cutString +=  cuts.get(i).getLength();
				if ( i < cuts.size() - 1 ) cutString += ", ";
			}
		}
		return cutString;
	}
}
