package ca.setc.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ca.setc.config.Preferences;
import ca.setc.dialogs.TwitterDialog;
import ca.setc.geocaching.AboutActivity;
import ca.setc.geocaching.Compass;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;
import ca.setc.geocaching.events.CompassUpdateEvent;
import ca.setc.geocaching.events.CompassUpdateEventListener;
import ca.setc.geocaching.events.DestinationChangedEvent;
import ca.setc.geocaching.events.DestinationChangedListener;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;
import ca.setc.parse.GeoLocation;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/**
 * Map activity. Displays the distance and bearing to a destination
 */
public class Map extends MapActivity implements LocationChangedListener,
		DestinationChangedListener, CompassUpdateEventListener {

	/** The GPS. */
	private GPS gps = GPS.getInstance();

	/** The MapController. */
	private MapController mc;
	private double prevYaw;
	private double prevBearing;
	private static final double REDRAW_DIFF = 5.0;

	/**
	 * Maximum distance from the destination a user is allowed to view and sign
	 * the log book in metres.
	 */
	private static final double LOGBOOK_RANGE = 2.5;

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(Map.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		log.debug("Entering map activity");
		if (Preferences.getBoolean("premium", false)) {
			setContentView(R.layout.activity_geo_caching);
		} else {
			setContentView(R.layout.activity_geo_caching_ads);
		}

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(false);
		mc = mapView.getController();

		gps.addLocationChangedListener(this);
		gps.setCurrentLocation(Preferences.getCurrentUser()
				.getCurrentLocation());

		gps.addDestinationChangedListener(this);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gps.setLocationManager(lm);

		Compass.getInstance().addChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_settings:
			intent = new Intent(this, UserSettings.class);
			startActivity(intent);
			return true;
		case R.id.menu_about:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.setc.geocaching.events.LocationChangedListener#locationChanged(ca.
	 * setc.geocaching.events.LocationChangedEvent)
	 */
	public void locationChanged(LocationChangedEvent event) {
		GeoLocation location = event.getLocation();
		log.debug("Location changed. Lat: {}, Long: {}",
				location.getLatitude(), location.getLongitude());
		mc.setCenter(location.toGeoPoint());

		Preferences.getCurrentUser().setCurrentLocation(location);

		updateDisplay(location, gps.getDestination());

		double newBearing = event.getLocation()
				.getBearing(gps.getDestination());
		if (Math.abs(newBearing - prevBearing) > REDRAW_DIFF) {
			prevBearing = newBearing;
			updateImages();
		}
	}

	/**
	 * Handles button clicks
	 * 
	 * @param v
	 *            the view of the clicked item
	 */
	public void onClick(View v) {

		log.debug("Button Clicked. Id: {}", v.getId());
		if (v.getId() == R.id.btn_add_dest_screen) {
			log.debug("Entering Add destination screen event");
			Intent intent = new Intent(this, AddDestinationActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_set_dest_screen) {
			log.debug("Entering set destination screen event");
			Intent intent = new Intent(this, SetDestinationActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_view_logbook) {
			log.debug("Entering view logbook screen event");
			Intent intent = new Intent(this, LogBookActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_sign_logbook) {
			log.debug("Entering sign logbook screen event");
			Intent intent = new Intent(this, SignLogBook.class);
			startActivity(intent);

			if (!Preferences.getBoolean("twitter_disabled", false)) {
				String m = String.format(getString(R.string.tweet),
						gps.getDestination());
				new TwitterDialog(this, "http://twitter.com/?status="
						+ Uri.encode(m)).show();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.setc.geocaching.events.DestinationChangedListener#destinationChanged
	 * (ca.setc.geocaching.events.DesinationChangedEvent)
	 */
	public void destinationChanged(DestinationChangedEvent event) {
		GeoLocation dest = event.getDestination();
		log.debug("Destination changed. Lat: {}, Long: {}", dest.getLatitude(),
				dest.getLongitude());
		updateDisplay(gps.getCurrentLocation(), dest);
		double newBearing = gps.getCurrentLocation().getBearing(
				event.getDestination());
		if (Math.abs(newBearing - prevBearing) > REDRAW_DIFF) {
			prevBearing = newBearing;
			updateImages();
		}
	}

	/**
	 * Update display.
	 * 
	 * @param location
	 *            the location
	 * @param destination
	 *            the destination
	 */
	private void updateDisplay(GeoLocation location, GeoLocation destination) {

		TextView distance = (TextView) findViewById(R.id.distance);
		if (location == null) {
			distance.setText(getString(R.string.await_gps));
			showLogBookButtons(false);
			return;
		} else if (destination == null) {
			distance.setText(getString(R.string.select_dest));
			showLogBookButtons(false);
			return;
		}

		double metres = location.getDistance(destination);

		if (metres <= LOGBOOK_RANGE) {
			showLogBookButtons(true);
		} else {
			showLogBookButtons(false);
		}

		String bearing = GPS.bearingToString(gps.getBearing(location));

		String distanceText = GPS.distanceToText(metres);

		log.debug("Bearing: {} As Text:{}", gps.getBearing(location), bearing);

		log.debug("Distance: {} As Text: {}", metres, distanceText);
		distance.setText(distanceText + " " + bearing);
	}

	/**
	 * Toggles logbook buttons
	 * 
	 * @param show
	 *            the show
	 */
	private void showLogBookButtons(boolean show) {
		Button viewLogBook = (Button) findViewById(R.id.btn_view_logbook);
		Button signLogBook = (Button) findViewById(R.id.btn_sign_logbook);

		if (show) {
			viewLogBook.setVisibility(View.VISIBLE);
			viewLogBook.setClickable(true);
			signLogBook.setVisibility(View.VISIBLE);
			signLogBook.setClickable(true);
		} else {
			viewLogBook.setVisibility(View.INVISIBLE);
			viewLogBook.setClickable(false);
			signLogBook.setVisibility(View.INVISIBLE);
			signLogBook.setClickable(false);
		}
	}

	public void compassUpdate(CompassUpdateEvent event) {
		float newYaw = event.getYaw();
		if (Math.abs(newYaw - prevYaw) > 2.5f) {
			prevYaw = newYaw;
			updateImages();
		}
	}

	private Bitmap rotateImage(Bitmap image, double degrees, boolean fixOrientation) {
		float orientationCorrection = 0.0f;
		WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		if(fixOrientation){
			switch (mDisplay.getOrientation()) {
			case (1):
				orientationCorrection = -90.0f;
				break;
			case (3):
				orientationCorrection = +90.0f;
				break;
			default:
				orientationCorrection = 0.0f;
				break;
			}
		}
		Matrix rotate = new Matrix();
		rotate.setRotate((float) (degrees + orientationCorrection),
				image.getHeight() / 2.0f, image.getWidth() / 2.0f);

		Bitmap rSprite = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
				image.getHeight(), rotate, true);

		return rSprite;
	}
	
	private void updateImages()
	{

		ImageView compass = (ImageView) findViewById(R.id.img_direction);
		Bitmap sprite = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.direction_raw);
		if(compass != null && sprite != null)
		{
			compass.setImageBitmap(rotateImage(sprite, (float) prevBearing, false));
			
		}

		compass = (ImageView) findViewById(R.id.img_compass);
		sprite = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.compass_raw);

		if(compass != null && sprite != null)
		{
			compass.setImageBitmap(rotateImage(sprite, -prevYaw + prevBearing, true));
			
		}
	}
}
