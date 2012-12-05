package ca.setc.logging;

import java.io.File;
import org.apache.log4j.Level;
import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Call {@link #configure()} from your application's activity.
 */
public final class ConfigureLog4J {

	private static final int DEFAULT_FILE_SIZE = 1024 * 512;
	private static final int DEFAULT_FILE_COUNT = 5;

	private ConfigureLog4J() {
	}

	/**
	 * Creates the inital setup for log4j
	 */
	public static void configure() { 
		final LogConfigurator logConfigurator = new LogConfigurator();

		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ File.separator + "geocaching.txt");
		logConfigurator.setRootLevel(Level.ALL);
		logConfigurator.setImmediateFlush(true);
		logConfigurator.setMaxBackupSize(DEFAULT_FILE_COUNT);
		logConfigurator.setMaxFileSize(DEFAULT_FILE_SIZE);

		logConfigurator.configure();
	}
}