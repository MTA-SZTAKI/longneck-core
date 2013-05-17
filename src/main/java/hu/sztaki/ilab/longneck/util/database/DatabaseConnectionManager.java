package hu.sztaki.ilab.longneck.util.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DatabaseConnectionManager {
    
    /** Map of configuration classes. */
    private static final Map<String,Class> configurationClasses = new HashMap<String,Class>();
    
    /** Database configurations. */
    private Map<String,Configuration> configurations;
    /** Runtime properties. */
    private Properties runtimeProperties;

    
    static {
        configurationClasses.put("jdbc", JdbcConfiguration.class);
    }
    
    public static void registerConfigurationClass(String type, Class c) {
        if (! Configuration.class.isAssignableFrom(c)) {
            throw new DatabaseConfigurationException(String.format(
                    "Class defined for database type %1$s, %2$s does not implement %3$s.", 
                    type, c.getName(), Configuration.class.getName()));
        }
        configurationClasses.put(type, c);
    }

    public DatabaseConnectionManager() {
        this.configurations = new HashMap<String,Configuration>();
    }
    
    public void afterPropertiesSet() throws DatabaseConfigurationException {
        // Search for entries 
        for (Map.Entry e : runtimeProperties.entrySet()) {
            String key = (String) e.getKey();
            String name;
            
            // Process entries that define database configuration type
            if (key.startsWith(("database.connection.")) && key.endsWith(".type")) {
                name = key.substring("database.connection.".length(), 
                        key.length() - ".type".length());
            
                if (name != null && ! "".equals(name)) {
                    String value = (String) e.getValue();
                    
                    // Instantiate appropriate configuration type based on value
                    if (configurationClasses.containsKey(value)) {
                        Configuration c;
                        
                        try {
                            c = (Configuration) configurationClasses.get(value).newInstance();
                            c.setName(name);
                            c.readProperites(runtimeProperties);
                            configurations.put(name, c);
                            
                        } catch (InstantiationException ex) {
                            throw new DatabaseConfigurationException(ex.getMessage(), ex);
                        } catch (IllegalAccessException ex) {
                            throw new DatabaseConfigurationException(ex.getMessage(), ex);
                        }
                    }                
                }
            }
        }
    }

    public Properties getRuntimeProperties() {
        return runtimeProperties;
    }

    public void setRuntimeProperties(Properties runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }
    
    public Configuration getConfiguration(String name) {
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        }
        
        throw new DatabaseConfigurationException(
                String.format("Database connection \"%1$s\" is not configured.", name));
    }
    
    public void destroy() {
        for (Configuration c : configurations.values()) {
            c.destroy();
        }
    }    
}
