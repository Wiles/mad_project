package ca.setc.geocaching.events;

public class CompassUpdateEvent {
	private float yaw;
	private float pitch;
	private float roll;
	private float incline;
	
	public CompassUpdateEvent(float yaw, float pitch, float roll, float incline)
	{
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.incline = incline;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}

	public float getIncline() {
		return incline;
	}
}
