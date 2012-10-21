package ca.setc.geocaching.events;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class LocationChangedEvent {
	
	private Location location;
	
	public LocationChangedEvent(Location location)
	{
		this.location = location;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public Double getLatitude()
	{
		return location.getLatitude();
	}
	
	public Double getLongitude()
	{
		return location.getLongitude();
	}
	
	public GeoPoint getGeoPoint()
	{
		return new GeoPoint((int)(location.getLatitude() * 1e6), (int)(location.getLongitude() * 1e6));
	}
}
