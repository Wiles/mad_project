package ca.setc.geocaching.events;

/**
 * The listener interface for receiving destinationChanged events. The class
 * that is interested in processing a destinationChanged event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addDestinationChangedListener<code> method. When
 * the destinationChanged event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see DestinationChangedEvent
 */
public interface DestinationChangedListener {

	/**
	 * Destination changed.
	 * 
	 * @param event
	 *            the event
	 */
	void destinationChanged(DestinationChangedEvent event);
}
