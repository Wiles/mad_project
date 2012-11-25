package ca.setc.geocaching.events;

/**
 * The listener interface for receiving locationChanged events. The class that
 * is interested in processing a locationChanged event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addLocationChangedListener<code> method. When
 * the locationChanged event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see LocationChangedEvent
 */
public interface LocationChangedListener {

	/**
	 * Location changed.
	 * 
	 * @param event
	 *            the event
	 */
	void locationChanged(LocationChangedEvent event);
}
