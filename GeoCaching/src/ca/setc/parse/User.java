package ca.setc.parse;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class User {
	
	private ParseUser parseUser;
	
	public User(ParseUser user)
	{
		this.parseUser = user;
	}
	
	public GeoLocation getCurrentLocation()
	{
		ParseGeoPoint location = parseUser.getParseGeoPoint("lastLocation");
		return new GeoLocation(location);
	}
	
	public void setCurrentLocation(GeoLocation location)
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
