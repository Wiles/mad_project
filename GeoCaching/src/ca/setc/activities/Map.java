package ca.setc.activities;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class Map extends MapActivity implements LocationChangedListener{

	private GPS gps = GPS.getInstance();
	protected MapController mc;

	private final Logger log = LoggerFactory.getLogger(Map.class);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.debug("{}", "Entering map activity");
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
        gps.setLocationManager(lm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_geo_caching, menu);
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void LocationChanged(LocationChangedEvent event) {
		log.debug("Location changed. Lat: {}, Long: {}", event.getLatitude(), event.getLongitude());
		mc.setCenter(event.getGeoPoint());
		Main.user.add("Location", new ParseGeoPoint(event.getLatitude(), event.getLongitude()));
		Main.user.saveEventually();
		TextView distance = (TextView)findViewById(R.id.distance);
		
		double metres = gps.getDistance(event.getLocation());
		String bearing = GPS.bearingToString(gps.getBearing(event.getLocation()));
		
		String distanceText;
		if(metres >= 1000)
		{
			distanceText = new DecimalFormat("#.#").format(metres/1000) + " km " + bearing;
		} 
		else 
		{
			distanceText = new DecimalFormat("#.#").format(metres) + " m " + bearing;
		}

		log.debug("Bearing: {} As Text:{}", gps.getBearing(event.getLocation()), bearing);

		log.debug("Distance: {} As Text: {}", metres, distanceText);
		distance.setText(distanceText);
	}
	

	public void onClick(View v) {

		log.debug("Button Clicked. Id: {}", v.getId());
		if (v.getId() == R.id.btn_add_dest_screen)
		{
			log.debug("Entering Add destination screen event");
			Dialog dia = new Dialog(this, R.layout.add_destination);
			dia.setContentView(R.layout.add_destination);
			dia.setCancelable(false);
			dia.setOwnerActivity(this);
			//TODO find out why this button is null
			Button btn = (Button)findViewById(R.id.btn_add_dest);
			btn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Double lat = Double.parseDouble(((EditText)findViewById(R.id.et_add_lat)).getText().toString());
					Double lng = Double.parseDouble(((EditText)findViewById(R.id.et_add_long)).getText().toString());

					log.debug("New Destination. Late:{}, Long:{}", lat, lng);
					ParseObject dest = new ParseObject("Destination");
					ParseGeoPoint pgp = new ParseGeoPoint(lat, lng);
					dest.add("location", pgp);
					dest.add("creator", Main.user);
					try {
						dest.save();
					} catch (ParseException e) {
						// TODO Auto-generated catch block

						log.error("Failed to save new destination", e);
					}
				}
			});
			dia.show();	
		}
	}
}
