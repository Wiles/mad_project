package com.mkyong.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class App2Activity extends Activity {

	Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
	}

}