package hu.sztaki.ilab.longneck.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class PropertyUtils {
    public static int getIntProperty(Properties properties, String propertyName, int defaultValue) {
        int value;
        try {
            value = Integer.parseInt(properties.getProperty(propertyName));
        } catch (NumberFormatException ex) {
            value = defaultValue;
        } catch (NullPointerException ex) {
            value = defaultValue;
        }
        
        return value;
    }
    
    public static boolean getBooleanProperty(Properties properties, String propertyName, boolean defaultValue) {
        boolean value;
        
        try {
            value = Boolean.parseBoolean(properties.getProperty(propertyName));
        } catch (NumberFormatException ex) {
            value = defaultValue;
        } catch (NullPointerException ex) {
            value = defaultValue;
        }
        
        return value;
    }
    
    public static List<String> getFilteredStringList(Properties properties, String propertyName, List<String> defaultValue) {
        List<String> retval = new ArrayList<String>();

        String value = properties.getProperty(propertyName);
        if (! StringUtils.isEmpty(value)) {
            String[] parts = StringUtils.split(value, ',');
            
            for (final String part : parts) {
                if (! StringUtils.isEmpty(part.trim())) {
                    retval.add(part.trim());
                }
            }
        }
        
        return retval;
    }
    
    public static Properties readPropertyFiles(File confDir, Set<String> blacklist) {
        if (! confDir.exists() || ! confDir.canRead()) {
            return new Properties();
        }
        
        Properties properties = new Properties();
        
        for (File f : confDir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".properties") && 
                    ! blacklist.contains(f.getName())) {
                try {
                    properties.load(new BufferedReader(new FileReader(f)));
                } catch (IOException ex) {
                    Logger.getLogger(PropertyUtils.class.getName()).warn(
                            String.format("Failed to read configuration file %1$s.", 
                            f.getAbsolutePath()), ex);
                    continue;
                }
            }
        }
        
        return properties;
    }
    
    public static Properties readDefaultProperties() {
        final Logger LOG = Logger.getLogger(PropertyUtils.class);
        
        Properties properties = new Properties();
        // Read defaults from classpath properties files
        PathMatchingResourcePatternResolver cpResolver = new PathMatchingResourcePatternResolver();
        try {
            for (Resource r : cpResolver.getResources("classpath*:META-INF/longneck/properties/*.properties")) {
                try {
                    Properties p = new Properties();
                    p.load(r.getInputStream());
                    properties.putAll(p);
                } catch (IOException ex) {
                    LOG.warn(String.format("Could not read properties file %1$s", 
                            r.getURL().toString()), ex);
                }
            }
        } catch (IOException ex) {
            LOG.warn("Failed to scan for default properties.", ex);
        }
        
        return properties;
    }
}
