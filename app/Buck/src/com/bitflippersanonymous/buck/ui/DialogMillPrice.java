package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DialogMillPrice extends DialogFragment implements OnEditorActionListener {
  static DialogMillPrice newInstance() {
      return new DialogMillPrice();
  }

  // Activity may not be fully created yet when onCreate is called. Override onActivityCreated instead
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
  	super.onActivityCreated(savedInstanceState);
  	getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {

  	View v = inflater.inflate(R.layout.dialog_mill_price, container, false);
  	EditText editText = (EditText)v.findViewById(R.id.price_value);
  	editText.setOnEditorActionListener(this);
    return v;
  }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			// Return input text to activity
			//EditNameDialogListener activity = (EditNameDialogListener) getActivity();
			//activity.onFinishEditDialog(mEditText.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}
}
