package ca.setc.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;
import ca.setc.geocaching.events.PhotoEvent;
import ca.setc.geocaching.events.PhotoListener;
import ca.setc.hardware.GPS;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Activity to create and share a new Destination
 */
public class AddDestinationActivity extends Activity implements PhotoListener {

	/** The log. */
	private final Logger log = LoggerFactory
			.getLogger(AddDestinationActivity.class);

	private Bitmap picture = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TakePictureActivity.addPhotoListener(this);

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
	 *            the view
	 */
	public void onClick(View v) {

		log.debug("Button Clicked. Id: {}", v.getId());
		if (v.getId() == R.id.btn_add_dest) {
			log.debug("Entering create dest event");

			EditText description = (EditText) findViewById(R.id.et_desc);
			EditText lat = (EditText) findViewById(R.id.et_add_lat);
			EditText lng = (EditText) findViewById(R.id.et_add_long);

			final ParseObject parse = new ParseObject("Destination");
			ByteArrayOutputStream stream = null;
			try {
				parse.put("creator", Preferences.getCurrentUser().toParseUser());
				parse.put("description", description.getText().toString());
				ParseGeoPoint location = new ParseGeoPoint(
						Double.parseDouble(lat.getText().toString()),
						Double.parseDouble(lng.getText().toString()));
				parse.put("location", location);
				if (picture != null) {
					stream = new ByteArrayOutputStream();
					picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();

					final ParseFile file = new ParseFile(byteArray);
					file.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException arg0) {
							parse.put("image", file);
							parse.saveEventually();
						}
					});

					finish();
				} else {
					parse.saveEventually();
					finish();
				}
			} catch (Exception ex) {
				log.error("Create desination failed: {}", ex.getMessage());

				Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ignore) {
						// ignore
					}
				}
			}
		} else if (v.getId() == R.id.btn_image) {
			if (picture == null) {
				log.debug("Entering add image event");
				Intent intent = new Intent(this, TakePictureActivity.class);
				startActivity(intent);
			} else {
				picture = null;
				ImageView image = (ImageView) findViewById(R.id.img_dest);

				Bitmap sprite = BitmapFactory.decodeResource(
						this.getResources(), R.drawable.ic_menu_camera);
				image.setImageBitmap(sprite);

				Button button = (Button) findViewById(R.id.btn_image);
				button.setText(getString(R.string.add_picture));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.setc.geocaching.events.PhotoListener#photoTaken(ca.setc.geocaching
	 * .events.PhotoEvent)
	 */
	public void photoTaken(final PhotoEvent event) {
		if (event.getFile() != null && event.getFile().exists()) {
			picture = BitmapFactory.decodeFile(event.getFile()
					.getAbsolutePath());
			runOnUiThread(new Runnable() {

				public void run() {
					ImageView image = (ImageView) findViewById(R.id.img_dest);
					image.setImageBitmap(picture);

					Button button = (Button) findViewById(R.id.btn_image);
					button.setText(getString(R.string.remove_picture));
				}
			});

		}
	}
}
