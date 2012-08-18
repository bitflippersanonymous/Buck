package com.bitflippersanonymous.buck.ui;

import com.bitflippersanonymous.buck.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		WebView webView = (WebView) findViewById(R.id.webViewAbout);
		webView.loadUrl(getResources().getString(R.string.about_url));
	}
}