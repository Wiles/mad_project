package ca.setc.geocaching;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import ca.setc.config.Preferences;
import ca.setc.geocaching.events.DesinationChangedEvent;
import ca.setc.geocaching.events.DestinationChangedListener;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;
import ca.setc.parse.GeoLocation;

public final class GPS {
	
	private static final double FEET_IN_METRE = 3.28084;
	
	private static final double DEGREES = 360.0;
	
	private static final double FEET_IN_MILE = 5280.0;
	
	private static final double METRE_IN_KILO = 1000.0;
	
	private static GPS instance;
	private GeoLocation currentLocation;
	private GeoLocation destination;
	private LocationManager lm;
	private LL ll = new LL();
	
	private List<LocationChangedListener> locationChangedListeners = new LinkedList<LocationChangedListener>();
	private List<DestinationChangedListener> destinationChangedListeners = new LinkedList<DestinationChangedListener>();

	private final Logger log = LoggerFactory.getLogger(GPS.class);
	
	private static String[] rosePoints = new String[]{
		"N", "NNE", "NE", "ENE",
		"E", "ESE", "SE", "SSE",
		"S", "SSW", "SW", "WSW",
		"W", "WNW", "NW", "NNW",
		};
	
	private GPS(){}
	
	public static GPS getInstance()
	{
		if(instance == null)
		{
			instance = new GPS();
		}
		return instance;
	}
	
	public void setLocationManager(LocationManager lm)
	{
		if(this.lm != null)
		{
			this.lm.removeUpdates(ll);
		}
		this.lm = lm;
        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}
	
	public void setCurrentLocation(GeoLocation location)
	{
		ll.onLocationChanged(location.toLocation());
	}
	
	public GeoLocation getCurrentLocation()
	{
		return currentLocation;
	}
	
	public GeoLocation getDestination()
	{
		return destination;
	}
	
	public void setDestination(GeoLocation destination)
	{
		this.destination = destination;
		for(DestinationChangedListener listener : destinationChangedListeners)
		{
			listener.destinationChanged(new DesinationChangedEvent(destination));
		}
	}
	
	public void addLocationChangedListener(LocationChangedListener listener)
	{
		locationChangedListeners.add(listener);
	}
	
	private class LL implements LocationListener {
		public void onLocationChanged(Location location) {
			log.debug("Location changed. lat:{}, long:{}", location.getLatitude(), location.getLongitude());
			currentLocation = new GeoLocation(location);
			for(LocationChangedListener listener : locationChangedListeners)
			{
				listener.locationChanged(new LocationChangedEvent(currentLocation));
			}
		}

		public void onProviderDisabled(String provider) {			
		}

		public void onProviderEnabled(String provider) {			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {			
		}
	}

	public Double getDistance(GeoLocation location) {
		if(this.destination == null || location == null)
		{
			return 0.0;
		}
		return (double) location.getDistance(destination);
	}

	public double getBearing(GeoLocation location) {
		if(this.destination == null || location == null){
			return 0.0;
		}
		return location.getBearing(destination);
	}
	
	public static String bearingToString(double bearing)
	{
		Double fixedBearing = bearing;
		if(fixedBearing < 0)
		{
			fixedBearing += DEGREES;
		}
		
		if(fixedBearing > DEGREES - (DEGREES/rosePoints.length/2) || fixedBearing <= (DEGREES/rosePoints.length/2))
		{
			return "N";
		}
		
		double min = (DEGREES/rosePoints.length/2);
		
		for(int i = 1; i < rosePoints.length; ++i)
		{
			if(fixedBearing > min && fixedBearing <= min + DEGREES/rosePoints.length )
			{
				return rosePoints[i];
			}
			else
			{
				min += DEGREES/rosePoints.length;
			}
			
		}
				
		return fixedBearing.toString();
	}
	
	public static String distanceToText(double metres)
	{
		boolean imperial = "imperial".equals(Preferences.get("units"));
		String unit = "m";
		double distance = 0;
		if(imperial)
		{
			double feet = metres * FEET_IN_METRE;
			if(feet >= FEET_IN_MILE)
			{
				unit = "m";
				distance = feet/FEET_IN_MILE;
			}
			else
			{
				unit = "ft";
				distance = feet;
			}
			
		}
		else
		{
			if(metres >= METRE_IN_KILO)
			{
				unit = "km";
				distance = metres/METRE_IN_KILO;
			} 
			else 
			{
				unit = "m";
				distance = metres;
			}
			
		}
		return new DecimalFormat("#.#").format(distance) + " " + unit;
		
	}

	public void addDestinationChangedListener(DestinationChangedListener listener) {
		destinationChangedListeners.add(listener);
	}
}
