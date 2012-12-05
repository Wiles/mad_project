package ca.setc.parse;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * Wrapper for a ParseUser
 */
public class User {

	/** The parse user. */
	private ParseUser parseUser;

	/**
	 * Instantiates a new user.
	 * 
	 * @param user
	 *            the user
	 */
	public User(ParseUser user) {
		this.parseUser = user;
	}

	/**
	 * Gets the current location.
	 * 
	 * @return the current location
	 */
	public GeoLocation getCurrentLocation() {
		ParseGeoPoint location = parseUser.getParseGeoPoint("lastLocation");
		return new GeoLocation(location);
	}

	/**
	 * Sets the current location.
	 * 
	 * @param location
	 *            the new current location
	 */
	public void setCurrentLocation(GeoLocation location) {
		parseUser.put(
				"lastLocation",
				new ParseGeoPoint(location.getLatitude(), location
						.getLongitude()));

		parseUser.saveEventually();
	}

	/**
	 * To parse user.
	 * 
	 * @return the parses the user
	 */
	public ParseUser toParseUser() {
		return this.parseUser;
	}
	
	/**
	 * Get the username
	 * 
	 * @return the username
	 */
	public String getUsername()
	{
		return this.parseUser.getUsername();
	}
}
