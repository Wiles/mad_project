package ca.setc.geocaching;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import ca.setc.config.Preferences;
import ca.setc.geocaching.events.DestinationChangedEvent;
import ca.setc.geocaching.events.DestinationChangedListener;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;
import ca.setc.parse.GeoLocation;

/**
 * The Class GPS. Abstracts the Android GPS implementation.
 */
public final class GPS {

	private static final double FEET_IN_METRE = 3.28084;

	private static final double DEGREES = 360.0;

	private static final double FEET_IN_MILE = 5280.0;

	private static final double METRE_IN_KILO = 1000.0;

	private static GPS instance;
	private GeoLocation currentLocation;
	private GeoLocation destination;
	private LocationManager lm;
	private LL ll = new LL();

	private List<LocationChangedListener> locationChangedListeners = new LinkedList<LocationChangedListener>();
	private List<DestinationChangedListener> destinationChangedListeners = new LinkedList<DestinationChangedListener>();

	private final Logger log = LoggerFactory.getLogger(GPS.class);

	private static String[] rosePoints = new String[] { "N", "NNE", "NE",
			"ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W",
			"WNW", "NW", "NNW", };

	private GPS() {
	}

	/**
	 * Get the singleton GPS instance
	 * 
	 * @return singleton GPS
	 */
	public static GPS getInstance() {
		if (instance == null) {
			instance = new GPS();
		}
		return instance;
	}

	/**
	 * Set the location manager to use
	 * 
	 * @param lm
	 *            location manager
	 */
	public void setLocationManager(LocationManager lm) {
		if (this.lm != null) {
			this.lm.removeUpdates(ll);
		}
		this.lm = lm;
		this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	/**
	 * Set the current location independently of GPS updates
	 * 
	 * @param location
	 *            of the user
	 */
	public void setCurrentLocation(GeoLocation location) {
		ll.onLocationChanged(location.toLocation());
	}

	/**
	 * Get the current location of the device
	 * 
	 * @return current location
	 */
	public GeoLocation getCurrentLocation() {
		return currentLocation;
	}

	/**
	 * Get the current target destination of the GPS
	 * 
	 * @return the destination
	 */
	public GeoLocation getDestination() {
		return destination;
	}

	/**
	 * Set the destination for the GPS
	 * 
	 * @param destination
	 *            of the GPS
	 */
	public void setDestination(GeoLocation destination) {
		this.destination = destination;
		for (DestinationChangedListener listener : destinationChangedListeners) {
			listener.destinationChanged(new DestinationChangedEvent(destination));
		}
	}

	/**
	 * Add a listener for when the location changes
	 * 
	 * @param listener
	 *            location changed
	 */
	public void addLocationChangedListener(LocationChangedListener listener) {
		locationChangedListeners.add(listener);
	}

	private class LL implements LocationListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onLocationChanged(android.location
		 * .Location)
		 */
		public void onLocationChanged(Location location) {
			log.debug("Location changed. lat:{}, long:{}",
					location.getLatitude(), location.getLongitude());
			currentLocation = new GeoLocation(location);
			for (LocationChangedListener listener : locationChangedListeners) {
				listener.locationChanged(new LocationChangedEvent(
						currentLocation));
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderDisabled(java.lang.String
		 * )
		 */
		public void onProviderDisabled(String provider) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */
		public void onProviderEnabled(String provider) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onStatusChanged(java.lang.String,
		 * int, android.os.Bundle)
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/**
	 * Get the distance in metres from the destination
	 * 
	 * @param location
	 *            from destination
	 * @return distance in metres, 0 if no current destination
	 */
	public Double getDistance(GeoLocation location) {
		if (this.destination == null || location == null) {
			return 0.0;
		}
		return (double) location.getDistance(destination);
	}

	/**
	 * Get the bearing to the current destination
	 * 
	 * @param location
	 * @return the bearing, 0 if no current destination
	 */
	public double getBearing(GeoLocation location) {
		if (this.destination == null || location == null) {
			return 0.0;
		}
		return location.getBearing(destination);
	}

	/**
	 * Returns a cardinal point representing the bearing
	 * 
	 * @param bearing
	 * @return cardinal point
	 */
	public static String bearingToString(double bearing) {
		Double fixedBearing = bearing;
		if (fixedBearing < 0) {
			fixedBearing += DEGREES;
		}

		if (fixedBearing > DEGREES - (DEGREES / rosePoints.length / 2)
				|| fixedBearing <= (DEGREES / rosePoints.length / 2)) {
			return "N";
		}

		double min = (DEGREES / rosePoints.length / 2);

		for (int i = 1; i < rosePoints.length; ++i) {
			if (fixedBearing > min
					&& fixedBearing <= min + DEGREES / rosePoints.length) {
				return rosePoints[i];
			} else {
				min += DEGREES / rosePoints.length;
			}

		}

		return fixedBearing.toString();
	}

	/**
	 * Changes distance in metre into formatted text.
	 * 
	 * Takes unit prefernece into account.
	 * 
	 * Uses ft/m for anything less than 1 kilometre/mile uses kilometre/miles
	 * for anything at or above that
	 * 
	 * @param metres
	 *            distance
	 * @return formatted distance string
	 */
	public static String distanceToText(double metres) {
		boolean imperial = "imperial".equals(Preferences.get("units"));
		String unit = "m";
		double distance = 0;
		if (imperial) {
			double feet = metres * FEET_IN_METRE;
			if (feet >= FEET_IN_MILE) {
				unit = "m";
				distance = feet / FEET_IN_MILE;
			} else {
				unit = "ft";
				distance = feet;
			}

		} else {
			if (metres >= METRE_IN_KILO) {
				unit = "km";
				distance = metres / METRE_IN_KILO;
			} else {
				unit = "m";
				distance = metres;
			}

		}
		return new DecimalFormat("#.#").format(distance) + " " + unit;

	}

	/**
	 * Add a listener for when the destination changes
	 * 
	 * @param listener
	 *            for destination change
	 */
	public void addDestinationChangedListener(
			DestinationChangedListener listener) {
		destinationChangedListeners.add(listener);
	}
}
