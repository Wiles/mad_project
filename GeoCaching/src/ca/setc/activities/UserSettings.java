package ca.setc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;

/**
 * Allows a user to manager there settings
 */
public class UserSettings extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_settings);

		CheckBox ana = (CheckBox) findViewById(R.id.cb_analytics);
		ana.setChecked(Preferences.getBoolean("analytics", false));

		CheckBox cb = (CheckBox) findViewById(R.id.cb_disable_twitter);
		cb.setChecked(Preferences.getBoolean("twitter_disabled", false));

		RadioButton imperial = (RadioButton) findViewById(R.id.radio_imperial);
		RadioButton metric = (RadioButton) findViewById(R.id.radio_metric);

		if ("metric".equals(Preferences.get("units"))) {
			metric.setChecked(true);
		} else {
			imperial.setChecked(true);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_settings, menu);
		return true;
	}

	/**
	 * Handles button clicks
	 * 
	 * @param v
	 *            the v
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.cb_analytics):
			CheckBox ana = (CheckBox) findViewById(R.id.cb_analytics);
			if (ana.isChecked()) {
				Preferences.setBoolean("analytics", true);
			} else {
				Preferences.setBoolean("analytics", false);
			}
			break;
		case (R.id.cb_disable_twitter):
			CheckBox cb = (CheckBox) findViewById(R.id.cb_disable_twitter);
			if (cb.isChecked()) {
				Preferences.setBoolean("twitter_disabled", true);
			} else {
				Preferences.setBoolean("twitter_disabled", false);
			}
			break;
		case (R.id.radio_imperial):
			Preferences.set("units", "imperial");
			break;
		case (R.id.radio_metric):
			Preferences.set("units", "metric");
			break;
		default:
		}
	}
}
