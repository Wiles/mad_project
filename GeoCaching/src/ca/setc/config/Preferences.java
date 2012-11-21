package ca.setc.config;

import android.content.SharedPreferences;

public class Preferences {
	
	private static SharedPreferences settings;
	
	private Preferences(){}
	
	public static void setSharedPreferences(SharedPreferences settings)
	{
		Preferences.settings = settings;
	}
	
	public static String get(String key)
	{
		if(settings != null)
		{
			return settings.getString(key, null);
		}
		return null;
	}
	
	public static void set(String key, String value)
	{
		if(settings != null)
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}
	
}
