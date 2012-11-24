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
import ca.setc.geocaching.events.DesinationChangedEvent;
import ca.setc.geocaching.events.DestinationChangedListener;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;
import ca.setc.parse.GeoLocation;

public class GPS {
	
	private static GPS instance;
	private GeoLocation currentLocation;
	private GeoLocation destination;
	private LocationManager lm;
	private LL ll = new LL();;
	
	private List<LocationChangedListener> locationChangedListeners = new LinkedList<LocationChangedListener>();
	private List<DestinationChangedListener> destinationChangedListeners = new LinkedList<DestinationChangedListener>();

	private final Logger log = LoggerFactory.getLogger(GPS.class);
	
	private static String[] rosePoints = new String[]{
		"N", "NNE", "NE", "ENE",
		"E", "ESE", "SE", "SSE",
		"S", "SSW", "SW", "WSW",
		"W", "WNW", "NW", "NNW",
		};
	
	private GPS()
	{
	}
	
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
	
	public static String bearingToString(Double bearing)
	{
		if(bearing < 0)
		{
			bearing += 360;
		}
		
		if(bearing > 360 - (360/rosePoints.length/2) || bearing <= (360/rosePoints.length/2))
		{
			return "N";
		}
		
		double min = (360/rosePoints.length/2);
		
		for(int i = 1; i < rosePoints.length; ++i)
		{
			if(bearing > min && bearing <= min + 360/rosePoints.length )
			{
				return rosePoints[i];
			}
			else
			{
				min += 360/rosePoints.length;
			}
			
		}
				
		return bearing.toString();
	}
	
	public static String distanceToText(double metres)
	{
		if(metres >= 1000)
		{
			return new DecimalFormat("#.#").format(metres/1000) + " km";
		} 
		else 
		{
			return new DecimalFormat("#.#").format(metres) + " m";
		}
	}

	public void addDestinationChangedListener(DestinationChangedListener listener) {
		destinationChangedListeners.add(listener);
	}
}
