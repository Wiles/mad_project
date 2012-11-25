package ca.setc.logging;

import ca.setc.config.Preferences;

import com.parse.ParseObject;

public final class Analytics {
	
	private Analytics(){}
	
	public static void send( String format, Object... args)
	{
		if(!Preferences.getBoolean("analytics", false))
		{
			return;
		}
				
		ParseObject message = new ParseObject("Analytics");
		message.put("message", String.format(format, args));
		message.saveEventually();
	}
	
}
