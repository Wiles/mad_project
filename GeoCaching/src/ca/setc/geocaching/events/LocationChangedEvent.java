package ca.setc.geocaching.events;

import ca.setc.parse.GeoLocation;

public class LocationChangedEvent {
	
	private GeoLocation location;
	
	public LocationChangedEvent(GeoLocation location)
	{
		this.location = location;
	}
	
	public GeoLocation getLocation()
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
}
