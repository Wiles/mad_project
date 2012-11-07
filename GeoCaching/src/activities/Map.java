package activities;

import java.text.DecimalFormat;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_caching);

    	Location destination = new Location("");
        destination.setLatitude(0.0);
        destination.setLongitude(0.0);
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
		
		distance.setText(distanceText);
	}
	

	public void onClick(View v) {
		if (v.getId() == R.id.btn_add_dest_screen)
		{
	    	setContentView(R.layout.add_destination);
		}
		else if (v.getId() == R.id.btn_add_dest)
		{
			try{
				Double lat = Double.parseDouble(((EditText)findViewById(R.id.et_add_lat)).getText().toString());
				Double lng = Double.parseDouble(((EditText)findViewById(R.id.et_add_long)).getText().toString());
				
				ParseObject dest = new ParseObject("Destination");
				ParseGeoPoint pgp = new ParseGeoPoint(lat, lng);
				dest.add("location", pgp);
				dest.add("creator", Main.user);
				try {
					dest.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			finally
			{
		    	setContentView(R.layout.activity_geo_caching);
			}
		}
	}
}
