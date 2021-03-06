package ca.setc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;

import com.parse.ParseObject;

/**
 * Used to inform the user of an un-handled exception and allow them to post the
 * error to the parse server
 */
public class UnhandledExceptionActivity extends Activity {

	/** The error message. */
	private String errorMessage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unhandled_exception);
		TextView view = (TextView) findViewById(R.id.tv_error);
		errorMessage = Preferences.get("error-to-log");
		view.setText(errorMessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	/**
	 * Handles button clicks
	 * 
	 * @param v
	 *            the v
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.btn_yes) {
			Toast.makeText(this, getString(R.string.thank_you),
					Toast.LENGTH_SHORT).show();
			try {
				ParseObject object = new ParseObject("UnhandledError");
				object.put("stacktrace", errorMessage);
				object.saveEventually();
			} catch (Exception ignore) {
				// ignore
			}
			finish();
		} else {
			finish();
		}
	}
}
