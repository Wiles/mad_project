package ca.setc.logging;

import java.io.File;
import org.apache.log4j.Level;
import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;
/**
 * Call {@link #configure()}} from your application's activity.
 */
public class ConfigureLog4J {
    public static void configure() {
        final LogConfigurator logConfigurator = new LogConfigurator();
                
        logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + "myapp.log");
        logConfigurator.setRootLevel(Level.ALL);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.setMaxBackupSize(5);
        logConfigurator.setMaxFileSize(1024 * 512);
        
        logConfigurator.configure();
    }
}