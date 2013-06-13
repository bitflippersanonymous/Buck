package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DialogMillPrice extends DialogFragment implements OnClickListener, OnEditorActionListener {
  static DialogMillPrice newInstance() {
      return new DialogMillPrice();
  }

  /*
  @Override
  public void onStart() {
  	super.onStart();
		InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
  	imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
  }
  */
  
  @Override
  public void onStop() {
  	super.onStop();
  	InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
  	imgr.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }
  
 
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {

  	View v = inflater.inflate(R.layout.dialog_mill_price, container, false);
  	EditText editText = (EditText)v.findViewById(R.id.price_value);
  	editText.setOnEditorActionListener(this);
  	
  	final EditText priceEditText = (EditText)v.findViewById(R.id.price_length);
  	
		for (int id : new int[]{R.id.buttonCancel, R.id.buttonSave} )
			v.findViewById(id).setOnClickListener(this);

		// Set focus on UI thread. Workaround for https://code.google.com/p/android/issues/detail?id=2705
		priceEditText.post(new Runnable() {
			@Override
			public void run() {
				priceEditText.requestFocus();
			}
		});
		
    return v;
  }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE ) {
			// Return input text to activity
			//EditNameDialogListener activity = (EditNameDialogListener) getActivity();
			//activity.onFinishEditDialog(mEditText.getText().toString());
			dismiss();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		dismiss();
	}
}
