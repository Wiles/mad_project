package ca.setc.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;
import ca.setc.hardware.GPS;
import ca.setc.parse.GeoLocation;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * View the summary of the destionation and set as the current destination
 */
public class ViewDesinationActivity extends Activity {

	/** The log. */
	private final Logger log = LoggerFactory
			.getLogger(ViewDesinationActivity.class);

	private ParseObject destination;

	/** The spinner progress. */
	private ProgressDialog mSpinner;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_desination);

		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(getString(R.string.loading));
		mSpinner.show();

		Intent intent = getIntent();
		ParseQuery query = new ParseQuery("Destination");
		query.getInBackground(intent.getStringExtra("locationId"),
				new GetCallback() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see com.parse.GetCallback#done(com.parse.ParseObject,
					 * com.parse.ParseException)
					 */
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							destination = object;
							TextView date = (TextView) findViewById(R.id.tv_date);
							TextView description = (TextView) findViewById(R.id.tv_description);
							date.setText(destination.getCreatedAt().toString());
							description.setText(destination
									.getString("description"));
							ParseFile image = (ParseFile) destination
									.get("image");
							if (image != null) {
								image.getDataInBackground(new GetDataCallback() {
									@Override
									public void done(byte[] data,
											ParseException ex) {
										if (ex == null) {
											Bitmap bmp = BitmapFactory
													.decodeByteArray(data, 0,
															data.length);
											ImageView iv = (ImageView) findViewById(R.id.img_dest);
											iv.setImageBitmap(bmp);
										} else {
											log.error(ex.getMessage(), ex);
										}

										mSpinner.dismiss();
									}
								});
							} else {
								mSpinner.dismiss();
							}
						} else {
							mSpinner.dismiss();
							Toast.makeText(getApplicationContext(),
									e.getMessage(), Toast.LENGTH_SHORT).show();
							Intent returnIntent = new Intent();
							setResult(RESULT_CANCELED, returnIntent);
							finish();
						}
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_view_desination, menu);
		return true;
	}

	/**
	 * Handles on click events that happen in the view
	 * 
	 * @param v
	 *            view of the click
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.btn_set_view):
			GeoLocation loc = new GeoLocation(
					(ParseGeoPoint) destination.get("location"));
			GPS.getInstance().setDestination(loc);
			Preferences.setDestination(destination);
			Intent returnIntent = new Intent();
			returnIntent.putExtra("result", 1);
			setResult(RESULT_OK, returnIntent);
			finish();
			break;
		}
	}
}
