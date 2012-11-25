package ca.setc.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ca.setc.config.Preferences;
import ca.setc.dialogs.TwitterDialog;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;
import ca.setc.geocaching.events.DesinationChangedEvent;
import ca.setc.geocaching.events.DestinationChangedListener;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;
import ca.setc.parse.GeoLocation;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.parse.ParseObject;

public class Map extends MapActivity implements LocationChangedListener, DestinationChangedListener{

	private GPS gps = GPS.getInstance();
	private MapController mc;
	
	private static ParseObject destination;
	
	private static final double LOGBOOK_RANGE = 5.0;

	private final Logger log = LoggerFactory.getLogger(Map.class);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log.debug("Entering map activity");
        setContentView(R.layout.activity_geo_caching);
		
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mc = mapView.getController();
        
        gps.addLocationChangedListener(this);
        gps.setCurrentLocation(Preferences.getCurrentUser().getCurrentLocation());
        
        gps.addDestinationChangedListener(this);
        
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gps.setLocationManager(lm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
    			Intent intent = new Intent(this, UserSettings.class);
    			startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void locationChanged(LocationChangedEvent event) {
		GeoLocation location = event.getLocation();
		log.debug("Location changed. Lat: {}, Long: {}", location.getLatitude(), location.getLongitude());
		mc.setCenter(location.toGeoPoint());
		
		Preferences.getCurrentUser().setCurrentLocation(location);
		
		updateDisplay(location, gps.getDestination());
	}
	

	public void onClick(View v) {

		log.debug("Button Clicked. Id: {}", v.getId());
		if (v.getId() == R.id.btn_add_dest_screen)
		{
			log.debug("Entering Add destination screen event");
			Intent intent = new Intent(this, AddDestinationActivity.class);
			startActivity(intent);
		} 
		else if (v.getId() == R.id.btn_set_dest_screen)
		{
			log.debug("Entering set destination screen event");
			Intent intent = new Intent(this, SetDestinationActivity.class);
			startActivity(intent);
		}
		else if(v.getId() == R.id.btn_view_logbook)
		{
			log.debug("Entering view logbook screen event");
			Intent intent = new Intent(this, LogBookActivity.class);
			startActivity(intent);
		}
		else if(v.getId() == R.id.btn_sign_logbook)
		{
			log.debug("Entering sign logbook screen event");
			Intent intent = new Intent(this, SignLogBook.class);
			startActivity(intent);

    		if(!Preferences.getBoolean("twitter_disabled", false))
    		{
    			String m = String.format(getString(R.string.tweet),new GeoLocation(Map.destination.getParseGeoPoint("location")));
        		new TwitterDialog(this,"http://twitter.com/?status="+Uri.encode(m)).show();
        		
        		
    		}
		}
	}

	public void destinationChanged(DesinationChangedEvent event) {
		GeoLocation dest = event.getDestination();
		log.debug("Destination changed. Lat: {}, Long: {}", dest.getLatitude(), dest.getLongitude());
		updateDisplay(gps.getCurrentLocation(), dest);
	}
	
	private void updateDisplay(GeoLocation location, GeoLocation destination)
	{

		TextView distance = (TextView)findViewById(R.id.distance);
		if(location == null)
		{
			distance.setText(getString(R.string.await_gps));
			showLogBookButtons(false);
			return;
		}
		else if(destination == null)
		{
			distance.setText(getString(R.string.select_dest));
			showLogBookButtons(false);
			return;
		}
		
		double metres = location.getDistance(destination);
		
		if(metres <= LOGBOOK_RANGE)
		{
			showLogBookButtons(true);
	        MapView mapView = (MapView) findViewById(R.id.mapview);
	        mapView.setBuiltInZoomControls(false);
		}
		else
		{
			showLogBookButtons(false);
	        MapView mapView = (MapView) findViewById(R.id.mapview);
	        mapView.setBuiltInZoomControls(true);
		}
		
		String bearing = GPS.bearingToString(gps.getBearing(location));
		
		String distanceText = GPS.distanceToText(metres);
		
		log.debug("Bearing: {} As Text:{}", gps.getBearing(location), bearing);

		log.debug("Distance: {} As Text: {}", metres, distanceText);
		distance.setText(distanceText + " " + bearing);
	}
	
	private void showLogBookButtons(boolean show)
	{
		Button viewLogBook = (Button)findViewById(R.id.btn_view_logbook);
		Button signLogBook = (Button)findViewById(R.id.btn_sign_logbook);
		
		if(show)
		{
			viewLogBook.setVisibility(View.VISIBLE);
			viewLogBook.setClickable(true);
			signLogBook.setVisibility(View.VISIBLE);
			signLogBook.setClickable(true);
		}
		else
		{
			viewLogBook.setVisibility(View.INVISIBLE);
			viewLogBook.setClickable(false);
			signLogBook.setVisibility(View.INVISIBLE);
			signLogBook.setClickable(false);
		}
	}
}
