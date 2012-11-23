package ca.setc.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class Map extends MapActivity implements LocationChangedListener{

	private GPS gps = GPS.getInstance();
	protected MapController mc;

	private final Logger log = LoggerFactory.getLogger(Map.class);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.debug("Entering map activity");
        setContentView(R.layout.activity_geo_caching);
    	Location destination = new Location("");
        destination.setLatitude(0.0);
        destination.setLongitude(0.0);
        log.debug("Initial Destination. lat:{} long:{}", 0.0, 0.0);
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mc = mapView.getController();
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gps.AddLocationChangedListener(this);
        gps.setDestination(destination);
        gps.setCurrentLocation(Main.user.getCurrentLocation());
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
                //TODO
                return true;
            case R.id.menu_add_destination:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void LocationChanged(LocationChangedEvent event) {
		log.debug("Location changed. Lat: {}, Long: {}", event.getLatitude(), event.getLongitude());
		mc.setCenter(event.getGeoPoint());
		
		Main.user.setCurrentLocation(event.getLocation());
		
		TextView distance = (TextView)findViewById(R.id.distance);
		
		double metres = gps.getDistance(event.getLocation());
		String bearing = GPS.bearingToString(gps.getBearing(event.getLocation()));
		
		String distanceText = GPS.distanceToText(metres);
		
		log.debug("Bearing: {} As Text:{}", gps.getBearing(event.getLocation()), bearing);

		log.debug("Distance: {} As Text: {}", metres, distanceText);
		distance.setText(distanceText + " " + bearing);
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
	}
}
