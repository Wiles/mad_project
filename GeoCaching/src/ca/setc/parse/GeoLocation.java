package ca.setc.parse;

import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.parse.ParseGeoPoint;


/**
 * Wrapper for ParseGeoPoint/GeoPooint/Location
 */
public class GeoLocation {
	
	/** The latitude. */
	private double latitude;
	
	/** The longitude. */
	private double longitude;
	
	/** The underlying ParseGeoPoint. */
	private ParseGeoPoint pgp;
	
	/** The Constant 1E6. */
	private static final double E6 = 1E6;
	
	/**
	 * Instantiates a new geo location.
	 *
	 * @param location the location
	 */
	public GeoLocation(Location location)
	{
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}
	
	/**
	 * Instantiates a new geo location.
	 *
	 * @param pgp the pgp
	 */
	public GeoLocation(ParseGeoPoint pgp)
	{
		latitude = pgp.getLatitude();
		longitude = pgp.getLongitude();
	}
	
	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public double getLatitude()
	{
		return latitude;
	}
	
	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude()
	{
		return longitude;
	}
	
	/**
	 * To location.
	 *
	 * @return the location
	 */
	public Location toLocation()
	{
		Location loc = new Location("");
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		return loc;
	}
	
	/**
	 * To geo point.
	 *
	 * @return the geo point
	 */
	public GeoPoint toGeoPoint()
	{
		return new GeoPoint((int)(latitude * E6), (int)(longitude * E6));
	}
	
	/**
	 * To parse geo point.
	 * 
	 * If this object was instantiated using a ParseGeoPoint
	 * then that point is return. Otherwise a new GeoPoint is returned.
	 *
	 * @return the parses geo point
	 */
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
	
	/**
	 * Gets the distance.
	 *
	 * @param destination the destination
	 * @return the distance
	 */
	public double getDistance(GeoLocation destination) {
		if(destination == null)
		{
			return 0.0;
		}
		Location dest = destination.toLocation();
		Location curr = toLocation();
		return (double) curr.distanceTo(dest);
	}

	/**
	 * Gets the bearing.
	 *
	 * @param destination the destination
	 * @return the bearing
	 */
	public double getBearing(GeoLocation destination) {
		if(destination == null)
		{
			return 0.0;
		}
		Location dest = destination.toLocation();
		Location curr = toLocation();
		return (double) curr.bearingTo(dest);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
