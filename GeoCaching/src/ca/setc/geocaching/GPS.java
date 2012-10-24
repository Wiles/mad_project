package ca.setc.geocaching;

import java.util.LinkedList;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;

public class GPS {
	
	private static GPS instance;
	
	private Location destination;
	private LocationManager lm;
	
	private List<LocationChangedListener> locationChangedListeners = new LinkedList<LocationChangedListener>();
	
	private static String[] rosePoints = new String[]{
		"N", "NNE", "NE", "ENE",
		"E", "ESE", "SE", "SSE",
		"S", "SSW", "SW", "WSW",
		"W", "WNW", "NW", "NNW",
		};
	
	private GPS()
	{
	}
	
	static GPS getInstance()
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
        LL ll = new LL();
        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}
	
	public Location getCurrentLocation()
	{
		//todo
		return new Location("");
	}
	
	public Location getDestination()
	{
		return destination;
	}
	
	public void setDestination(Location destination)
	{
		this.destination = destination;
	}
	
	public void AddLocationChangedListener(LocationChangedListener listener)
	{
		locationChangedListeners.add(listener);
	}
	
	private class LL implements LocationListener {
		public void onLocationChanged(Location location) {
			for(LocationChangedListener listener : locationChangedListeners)
			{
				listener.LocationChanged(new LocationChangedEvent(location));
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}

	public Double getDistance(Location location) {
		if(this.destination == null || location == null)
		{
			return 0.0;
		}
		return (double) location.distanceTo(destination);
	}

	public double getBearing(Location location) {
		if(this.destination == null || location == null){
			return 0.0;
		}
		return location.bearingTo(destination);
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
}
