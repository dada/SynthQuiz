package it.perl.dada.SynthQuiz;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);		

		WebView about_text = (WebView)this.findViewById(R.id.about_text);
		about_text.loadUrl("file:///android_asset/about.html");
		/*
		String html = getResources().getString(R.string.about_text);
		about_text.loadDataWithBaseURL(
			null, html, "text/html", "utf-8", null
		);
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_about, menu);
		return true;
	}

}
