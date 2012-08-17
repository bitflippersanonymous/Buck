package com.bitflippersanonymous.buck;

import com.bitflippersanonymous.buck.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class BuckAboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		WebView webView = (WebView) findViewById(R.id.webViewAbout);
		webView.loadUrl(getResources().getString(R.string.about_url));
	}
}