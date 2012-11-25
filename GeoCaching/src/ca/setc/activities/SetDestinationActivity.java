package ca.setc.activities;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;
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
	 * TODO
	 * 
	 * @param l
	 *            the l
	 * @param v
	 *            the v
	 * @param position
	 *            the position
	 * @param id
	 *            the id
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		log.info("Destination selected: {}", position);
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
			public void done(List<ParseObject> arg0, ParseException arg1) {
				locations.clear();
				if (arg1 != null) {
					Toast.makeText(null, arg1.getMessage(), Toast.LENGTH_LONG)
							.show();
				}

				ListView lv = (ListView) findViewById(R.id.lv_destinations);

				String[] str = new String[arg0.size()];
				for (int i = 0; i < arg0.size(); ++i) {
					ParseObject obj = arg0.get(i);
					locations.add(obj);
					GeoLocation dest = new GeoLocation((ParseGeoPoint) obj
							.get("location"));
					GeoLocation curr = Preferences.getCurrentUser()
							.getCurrentLocation();
					str[i] = String.format(getString(R.string.destination_description),
							GPS.distanceToText(curr.getDistance(dest)),
							GPS.bearingToString(curr.getBearing(dest)),
							obj.get("description"));
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SetDestinationActivity.this,
						android.R.layout.simple_list_item_1,
						android.R.id.text1, str);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * android.widget.AdapterView.OnItemClickListener#onItemClick
					 * (android.widget.AdapterView, android.view.View, int,
					 * long)
					 */
					public void onItemClick(AdapterView<?> adater, View view,
							int position, long id) {
						GPS.getInstance().setDestination(
								new GeoLocation((ParseGeoPoint) locations.get(
										position).get("location")));
						Preferences.setDestination(locations.get(position));
						SetDestinationActivity.this.finish();
					}
				});
				mSpinner.dismiss();
			}
		});
	}
}
