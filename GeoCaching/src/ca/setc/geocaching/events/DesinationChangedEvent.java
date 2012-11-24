package ca.setc.geocaching.events;

import ca.setc.parse.GeoLocation;

public class DesinationChangedEvent {
	
	private GeoLocation destination;
	
	public DesinationChangedEvent(GeoLocation destination)
	{
		this.destination = destination;
	}
	
	public GeoLocation getDestination()
	{
		return destination;
	}
	
}
