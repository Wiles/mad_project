package ca.setc.geocaching.events;

import java.io.File;

public class PhotoEvent {
	File file;

	public PhotoEvent(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}
}
