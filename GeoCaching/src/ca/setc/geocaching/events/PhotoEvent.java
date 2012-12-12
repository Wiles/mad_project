package ca.setc.geocaching.events;

import java.io.File;

/**
 * The Class PhotoEvent.
 */
public class PhotoEvent {

	private File file;

	/**
	 * Constructor
	 * 
	 * @param file
	 *            the file
	 */
	public PhotoEvent(File file) {
		this.file = file;
	}

	/**
	 * Returns the file associated with the photo event
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
}
