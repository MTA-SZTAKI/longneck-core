package hu.sztaki.ilab.longneck.util;

import java.io.File;

/**
 *
 * @author Molnár Péter <molnar.peter@sztaki.mta.hu>
 */
public class OsUtils {

    public static String getHomeDirectoryPath(OsType type) {
        if (type == OsType.Linux) {
            return String.format("%1$s%2$s%3$s", System.getProperty("user.home"), "/", ".longneck");
        }
              
        return String.format("%1$s%2$s%3$s", System.getProperty("user.home"), "/", "longneck");        
    }
}
