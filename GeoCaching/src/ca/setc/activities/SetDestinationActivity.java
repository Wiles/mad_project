package ca.setc.activities;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;
import ca.setc.hardware.GPS;
import ca.setc.parse.GeoLocation;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity to select a destination from a list
 */
public class SetDestinationActivity extends Activity {

	/** The log. */
	private final Logger log = LoggerFactory
			.getLogger(SetDestinationActivity.class);

	/** The locations. */
	private List<ParseObject> locations = new ArrayList<ParseObject>();

	/** The Constant MAX_DESTINATION. */
	private static final int MAX_DESTINATION = 10;

	/** The m spinner. */
	private ProgressDialog mSpinner;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_destination);

		mSpinner = ProgressDialog.show(this, "", getString(R.string.loading));

		loadNear();
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
	 * Load the closest point for display
	 */
	private void loadNear() {
		ParseGeoPoint userLocation = Preferences.getCurrentUser()
				.getCurrentLocation().toParseGeoPoint();
		ParseQuery query = new ParseQuery("Destination");
		query.whereNear("location", userLocation);
		query.setLimit(MAX_DESTINATION);
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> list, ParseException exception) {
				if (exception != null) {
					Toast.makeText(SetDestinationActivity.this,
							exception.getMessage(), Toast.LENGTH_LONG).show();
				} else {
					loadList(list);
				}
			}
		});
	}

	/**
	 * Load a list of destination onto the screen
	 * 
	 * @param list
	 *            the list
	 */
	private void loadList(List<ParseObject> list) {
		locations.clear();

		ListView lv = (ListView) findViewById(R.id.lv_destinations);

		String[] str = new String[list.size()];
		for (int i = 0; i < list.size(); ++i) {
			ParseObject obj = list.get(i);
			locations.add(obj);
			GeoLocation dest = new GeoLocation(
					(ParseGeoPoint) obj.get("location"));
			GeoLocation curr = Preferences.getCurrentUser()
					.getCurrentLocation();
			str[i] = String.format(getString(R.string.destination_description),
					GPS.distanceToText(curr.getDistance(dest)),
					GPS.bearingToString(curr.getBearing(dest)),
					obj.get("description"));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				SetDestinationActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1, str);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick
			 * (android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> adater, View view,
					int position, long id) {
				itemClick(position);
			}
		});
		mSpinner.dismiss();

	}

	/**
	 * Updates the GPS and preferences with the new location
	 * 
	 * @param position
	 *            index of item clicked
	 */
	private void itemClick(int position) {
		GeoLocation loc = new GeoLocation((ParseGeoPoint) locations.get(
				position).get("location"));
		log.info("Item clicked{}", loc);
		Intent intent = new Intent(this, ViewDesinationActivity.class);
		intent.putExtra("locationId", locations.get(position).getObjectId());
		startActivityForResult(intent, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			finish();
		}
	}
}
