package ca.setc.geocaching.events;

import ca.setc.parse.GeoLocation;

/**
 * Contains information related to a destination change
 */
public class DestinationChangedEvent {
	
	/** The destination. */
	private GeoLocation destination;
	
	/**
	 * Instantiates a new destination changed event.
	 *
	 * @param destination the destination
	 */
	public DestinationChangedEvent(GeoLocation destination)
	{
		this.destination = destination;
	}
	
	/**
	 * Gets the destination.
	 *
	 * @return the destination
	 */
	public GeoLocation getDestination()
	{
		return destination;
	}
	
}
