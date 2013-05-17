package hu.sztaki.ilab.longneck.util.database;

import java.util.Properties;

/**
 * Interface for configuration beans.
 */
public interface Configuration {
    
    public void setName(String name);
    public void readProperites(Properties properties) throws DatabaseConfigurationException;
    public void destroy();
    
}
    
