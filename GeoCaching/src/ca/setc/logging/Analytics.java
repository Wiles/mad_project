package ca.setc.logging;

import ca.setc.config.Preferences;

import com.parse.ParseObject;

/**
 * Sends anonymous usage data to the parse server if the user has opted in
 */
public final class Analytics {

	/**
	 * Instantiates a new analytics.
	 */
	private Analytics() {
	}

	/**
	 * Send a message.
	 * 
	 * @param format
	 *            the format
	 * @param args
	 *            the args
	 */
	public static void send(String format, Object... args) {
		if (!Preferences.getBoolean("analytics", false)) {
			return;
		}

		ParseObject message = new ParseObject("Analytics");
		message.put("message", String.format(format, args));
		message.saveEventually();
	}

}
