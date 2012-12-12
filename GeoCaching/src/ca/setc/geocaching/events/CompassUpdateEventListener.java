package ca.setc.geocaching.events;

/**
 * The listener interface for receiving compassUpdateEvent events. The class
 * that is interested in processing a compassUpdateEvent event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addCompassUpdateEventListener<code> method. When
 * the compassUpdateEvent event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see CompassUpdateEventEvent
 */
public interface CompassUpdateEventListener {

	/**
	 * Compass update.
	 * 
	 * @param event
	 *            the event
	 */
	void compassUpdate(CompassUpdateEvent event);
}
