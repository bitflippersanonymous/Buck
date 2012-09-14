package com.bitflippersanonymous.buck.domain;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bitflippersanonymous.buck.domain.Price.Fields;


public class PriceAdapter extends ArrayAdapter<Price> implements ListAdapter {

	public PriceAdapter(Context context, int textViewResourceId,
			List<Price> objects) {
		super(context, textViewResourceId, objects);
	}

	/*TODO: Create Column headings
	Join columns together better (Or make separate TextViews)
	DBQuery that populates this list should filter by millId.
		Or not... Could do one query and store all data and filter display.
		Depends on how many mills there are
	*/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if ( view == null ) {
			view = LayoutInflater.from(parent.getContext()).inflate(
				android.R.layout.simple_list_item_1, parent, false);
		}
		
		Price price = getItem(position);
		String text = new String();
		text += price.getAsString(Fields.Length) + "'  ";
		String rate = price.getAsString(Fields.Rate);
		if ( rate != null )
			text += rate + "%  ";
		text += "$" + price.getAsString(Fields.Price) + " MBF";
		
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(text);
		return view;
	}
	
	// TODO: Add 'add' action button to add new price
	// TODO: List onItemSelected to edit current price
}
