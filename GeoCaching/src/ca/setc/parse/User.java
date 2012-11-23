package ca.setc.parse;

import android.location.Location;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class User {
	
	private ParseUser parseUser;
	
	public User(ParseUser user)
	{
		this.parseUser = user;
	}
	
	public Location getCurrentLocation()
	{
		ParseGeoPoint location = parseUser.getParseGeoPoint("lastLocation");
		Location loc = new Location("");
		if(location == null)
		{
			loc.setLatitude(0.0);
			loc.setLongitude(0.0);
		}
		else
		{
			loc.setLatitude(location.getLatitude());
			loc.setLongitude(location.getLongitude());
		}
		return loc;
	}
	
	public void setCurrentLocation(Location location)
	{
		try
		{
			parseUser.put("lastLocation", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
			
			parseUser.saveInBackground();
		}
		catch(Exception ex)
		{
			//TODO
		}
	}
	
	public ParseUser toParseUser()
	{
		return this.parseUser;
	}
}
