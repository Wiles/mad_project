package ca.setc.geocaching;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import ca.setc.geocaching.events.CompassUpdateEvent;
import ca.setc.geocaching.events.CompassUpdateEventListener;

public final class Compass implements SensorEventListener {

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(Compass.class);

	private static SensorManager sensorManager;
	private static Compass instance = new Compass();

	private static double rad2deg = (double) (180.0 / Math.PI);
	private DateTime lastUpdate = new DateTime();

	private float[] accelerometerData = new float[3];
	private float[] magneticFieldData = new float[3];
	private float[] mR = new float[16];
	private float[] mI = new float[16];
	private float[] orientation = new float[3];
	private List<CompassUpdateEventListener> listeners = new ArrayList<CompassUpdateEventListener>();

	private Compass() {
	}

	public static Compass getInstance() {
		return instance;
	}

	public void addChangeListener(CompassUpdateEventListener listener) {
		listeners.add(listener);
	}

	public void setSensorManager(SensorManager manager) {
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
		sensorManager = manager;
		Sensor gsensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		Sensor msensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, gsensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, msensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing
	}

	public void onSensorChanged(SensorEvent event) {
		long diff = DateTime.now().getMillis() - lastUpdate.getMillis();
		if (diff > 250) {
			lastUpdate = DateTime.now();

			int type = event.sensor.getType();
			float[] data;
			if (type == Sensor.TYPE_ACCELEROMETER) {
				data = accelerometerData;
			} else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
				data = magneticFieldData;
			} else {
				// we should not be here.
				return;
			}
			for (int i = 0; i < 3; i++)
				data[i] = event.values[i];

			SensorManager.getRotationMatrix(mR, mI, accelerometerData,
					magneticFieldData);
			SensorManager.getOrientation(mR, orientation);
			float incline = (float) (SensorManager.getInclination(mI) * rad2deg);
			float yaw = (float) (orientation[0] * rad2deg);
			float pitch = (float) (orientation[1] * rad2deg);
			float roll = (float) (orientation[2] * rad2deg);

			log.info("Yaw: {}. Pitch: {}. Roll: {}. Inline: {}.", new Object[] {
					yaw, pitch, roll, incline });

			for (CompassUpdateEventListener listener : listeners) {
				listener.compassUpdate(new CompassUpdateEvent(yaw, pitch, roll,
						incline));
			}
		}
	}
}
