package com.bitflippersanonymous.buck.domain;

import java.util.List;

import com.bitflippersanonymous.buck.R;
import com.bitflippersanonymous.buck.domain.Price;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


public class PriceAdapter extends ArrayAdapter<Price> implements ListAdapter {

	public PriceAdapter(Context context, int textViewResourceId,
			List<Price> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if ( view == null )
			view = new View(parent.getContext());
		return view;
	}
}
