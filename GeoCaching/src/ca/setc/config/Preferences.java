package ca.setc.config;

import com.parse.ParseObject;

import ca.setc.parse.User;
import android.content.SharedPreferences;

/**
 * User preferences
 */
public final class Preferences {

	/** The settings. */
	private static SharedPreferences settings;

	/** The current user. */
	private static User currentUser;
	
	/** The destination parse object*/
	private static ParseObject destination;

	/**
	 * Instantiates a new preferences.
	 */
	private Preferences() {
	}

	/**
	 * Sets the shared preferences.
	 * 
	 * @param settings
	 *            the new shared preferences
	 */
	public static void setSharedPreferences(SharedPreferences settings) {
		Preferences.settings = settings;
	}

	/**
	 * Sets the current user.
	 * 
	 * @param user
	 *            the new current user
	 */
	public static void setCurrentUser(User user) {
		currentUser = user;
	}

	/**
	 * Gets the current user.
	 * 
	 * @return the current user
	 */
	public static User getCurrentUser() {
		return currentUser;
	}
	
	/**
	 * Sets the destination.
	 *
	 * @return the parses the object
	 */
	public static void setDestination(ParseObject destination)
	{
		Preferences.destination = destination;
	}
	
	public static ParseObject getDestination()
	{
		return destination;
	}

	/**
	 * Gets the string preference based on key.
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	public static String get(String key) {
		if (settings != null) {
			return settings.getString(key, null);
		}
		return null;
	}

	/**
	 * Sets a string preference.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public static void set(String key, String value) {
		if (settings != null) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	/**
	 * Sets a boolean preferences
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public static void setBoolean(String key, boolean value) {
		if (settings != null) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(key, value);
			editor.commit();
		}
	}

	/**
	 * Gets a boolean preference
	 * 
	 * @param key
	 *            the key
	 * @param defVal
	 *            the default value
	 * @return the boolean
	 */
	public static boolean getBoolean(String key, boolean defVal) {
		if (settings != null) {
			return settings.getBoolean(key, defVal);
		}
		return defVal;
	}
}
