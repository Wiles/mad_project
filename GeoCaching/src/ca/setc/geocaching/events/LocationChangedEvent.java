package ca.setc.geocaching.events;

import ca.setc.parse.GeoLocation;

/**
 * Contains information pertaining to a location change
 */
public class LocationChangedEvent {
	
	/** The location. */
	private GeoLocation location;
	
	/**
	 * Instantiates a new location changed event.
	 *
	 * @param location the location
	 */
	public LocationChangedEvent(GeoLocation location)
	{
		this.location = location;
	}
	
	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public GeoLocation getLocation()
	{
		return location;
	}
}
