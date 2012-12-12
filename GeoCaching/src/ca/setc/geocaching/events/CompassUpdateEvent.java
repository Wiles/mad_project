package ca.setc.geocaching.events;

/**
 * Information associated with a Compass Update event
 */
public class CompassUpdateEvent {

	private float yaw;

	private float pitch;

	private float roll;

	private float incline;

	/**
	 * Constructor
	 * 
	 * @param yaw
	 * @param pitch
	 * @param roll
	 * @param incline
	 */
	public CompassUpdateEvent(float yaw, float pitch, float roll, float incline) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.incline = incline;
	}

	/**
	 * Gets the yaw.
	 * 
	 * @return the yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Gets the pitch.
	 * 
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Gets the roll.
	 * 
	 * @return the roll
	 */
	public float getRoll() {
		return roll;
	}

	/**
	 * Gets the incline.
	 * 
	 * @return the incline
	 */
	public float getIncline() {
		return incline;
	}
}
