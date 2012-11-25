package ca.setc.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Activity to create and share a new Destination
 */
public class AddDestinationActivity extends Activity {

	/** The log. */
	private final Logger log = LoggerFactory
			.getLogger(AddDestinationActivity.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_destination);

		EditText lat = (EditText) findViewById(R.id.et_add_lat);
		lat.setText(Double.toString(GPS.getInstance().getCurrentLocation()
				.getLatitude()));
		EditText lng = (EditText) findViewById(R.id.et_add_long);
		lng.setText(Double.toString(GPS.getInstance().getCurrentLocation()
				.getLongitude()));
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

		log.debug("Button Clicked. Id: {}", v.getId());
		if (v.getId() == R.id.btn_add_dest) {
			log.debug("Entering create dest event");

			EditText description = (EditText) findViewById(R.id.et_desc);
			EditText lat = (EditText) findViewById(R.id.et_add_lat);
			EditText lng = (EditText) findViewById(R.id.et_add_long);

			ParseObject parse = new ParseObject("Destination");
			try {
				parse.put("creator", Preferences.getCurrentUser().toParseUser());
				parse.put("description", description.getText().toString());
				ParseGeoPoint location = new ParseGeoPoint(
						Double.parseDouble(lat.getText().toString()),
						Double.parseDouble(lng.getText().toString()));
				parse.put("location", location);
				parse.save();
				finish();
			} catch (Exception ex) {
				log.error("Create desination failed: {}", ex.getMessage());

				Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}
}
