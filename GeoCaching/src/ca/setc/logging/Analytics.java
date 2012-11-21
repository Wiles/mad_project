package ca.setc.logging;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import ca.setc.activities.Main;

import com.parse.ParseObject;

public class Analytics {
	
	private static boolean enabled = true;
	
	private static boolean anonymous = true;
	
	private Analytics(){}
	
	public static void send( String format, Object... args)
	{
		if(!enabled)
		{
			return;
		}
				
		ParseObject message = new ParseObject("Analytics");
		DateTime dt = new DateTime();
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		String str = fmt.print(dt);
		message.put("date", str);
		if(!anonymous && Main.user != null)
		{
			message.put("user", Main.user);
		}
		message.put("message", String.format(format, args));
		message.saveEventually();
	}
	
}
