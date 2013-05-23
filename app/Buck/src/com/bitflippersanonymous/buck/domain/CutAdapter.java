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
		StringBuilder cutString = new StringBuilder();
		makeCutString(node, cutString);
		tc.setText(cutString);
		TextView tb = (TextView)convertView.findViewById(R.id.textViewCutBf);
		tb.setText(Integer.valueOf(node.getTotalBoardFeet()).toString());
		TextView tv = (TextView)convertView.findViewById(R.id.textViewCutValue);
		tv.setText("$"+Integer.valueOf(node.getTotalValue()).toString());
		return convertView;
	}

	private void makeCutString(CutNode node, StringBuilder cutString) {
		if ( node.getParent().getDimension() != null )
			makeCutString(node.getParent(), cutString);
		
		// Scrap
		if ( node.getValue() == 0 )
			cutString.append("+");

		cutString.append(node.getDimension().getLength());
		if ( node.getChildren().size() != 0 )
			cutString.append(", ");
	}
}
