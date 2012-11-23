package ca.setc.parse;

import android.location.Location;

import com.parse.ParseGeoPoint;

public class GeoLocation {
	double latitude;
	double longitude;
	ParseGeoPoint pgp;
	
	public GeoLocation(Location location)
	{
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}
	
	public GeoLocation(ParseGeoPoint pgp)
	{
		latitude = pgp.getLatitude();
		longitude = pgp.getLongitude();
	}
	
	public Location toLocation()
	{
		Location loc = new Location("");
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		return loc;
	}
	
	public ParseGeoPoint toParseGeoPoint()
	{
		pgp.setLatitude(latitude);
		pgp.setLongitude(longitude);
		return pgp;
	}
	
	public double getDistance(GeoLocation destination) {
		if(destination == null)
		{
			return 0.0;
		}
		Location dest = destination.toLocation();
		Location curr = toLocation();
		return (double) curr.distanceTo(dest);
	}

	public double getBearing(GeoLocation destination) {
		if(destination == null)
		{
			return 0.0;
		}
		Location dest = destination.toLocation();
		Location curr = toLocation();
		return (double) curr.bearingTo(dest);
	}
}
