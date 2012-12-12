package ca.setc.geocaching.events;

/**
 * The listener interface for receiving photo events. The class that is
 * interested in processing a photo event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addPhotoListener<code> method. When
 * the photo event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see PhotoEvent
 */
public interface PhotoListener {

	/**
	 * Photo taken.
	 * 
	 * @param event
	 *            the event
	 */
	void photoTaken(PhotoEvent event);
}
