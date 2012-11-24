package ca.setc.parse;

import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.parse.ParseGeoPoint;

public class GeoLocation {
	private double latitude;
	private double longitude;
	private ParseGeoPoint pgp;
	
	private static final double E6 = 1E6;
	
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
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public Location toLocation()
	{
		Location loc = new Location("");
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		return loc;
	}
	
	public GeoPoint toGeoPoint()
	{
		return new GeoPoint((int)(latitude * E6), (int)(longitude * E6));
	}
	
	public ParseGeoPoint toParseGeoPoint()
	{
		if(pgp == null)
		{
			pgp = new ParseGeoPoint();
		}
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
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		Location loc = toLocation();
		
		sb.append(Location.convert(loc.getLatitude(), Location.FORMAT_SECONDS));
		sb.append(" ");
		sb.append(Location.convert(loc.getLongitude(), Location.FORMAT_SECONDS));
		
		return sb.toString();
	}
}
