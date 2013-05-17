package hu.sztaki.ilab.longneck.util.database;

import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * JDBC configuration bean.
 */
public class JdbcConfiguration implements Configuration {
    
    /** Name of connection. */
    private String name;
    /** The connection properties. */
    private Properties properties;
    
    /** The instantiated data source. */
    private BasicDataSource dataSource = null;

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void readProperites(Properties properties) throws DatabaseConfigurationException {
        if (name == null || "".equals(name)) {
            throw new DatabaseConfigurationException("JDBC connection name cannot be null.");
        }

        this.properties = new Properties();
        String prefix = String.format("database.connection.%1$s.", name);
        int len = prefix.length();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                String shortKey = key.substring(len);
                if ("name".equals(shortKey) || "type".equals(shortKey)) {
                    continue;
                }
                
                this.properties.setProperty(shortKey, properties.getProperty(key));
            }
        }

        if (! this.properties.containsKey("url")) {
            throw new DatabaseConfigurationException(String.format(
                    "JDBC connection property database.connection.%1$s.url is undefined.", name));
        }

        if (! this.properties.containsKey("username")) {
            throw new DatabaseConfigurationException(String.format(
                    "JDBC connection property database.connection.%1$s.username is undefined.", 
                    name));
        }
    }

    public DataSource getDataSource() {            
        if (dataSource == null) {                        
            String driverClass = properties.getProperty("driverClassName", null);
            if (driverClass != null && ! "".equals(driverClass)) {
                // Load driver class
                try {
                    Class.forName(driverClass);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(
                            String.format("Could not load driver class for database connection %1$s: %2$s.", 
                            name, driverClass), ex);
                }
            }
            try {
                dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);
            } catch (Exception ex) {
                throw new RuntimeException(
                        String.format("Could not create JDBC datasource %1$s.", name), ex);
            }
        }

        return dataSource;
    }

    @Override
    public void destroy() {
        try {
            dataSource.close();
        } catch (SQLException ex) {
            throw new RuntimeException(String.format("Error closing datasource %1$s.", name), ex);
        } catch (NullPointerException ex) {
            // do nothing
        }    
    }
    
    
}
